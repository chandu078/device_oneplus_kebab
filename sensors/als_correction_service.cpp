/*
 * Copyright (C) 2021 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "AsyncScreenCaptureListener.h"

#include <android-base/properties.h>
#include <binder/IBinder.h>
#include <binder/ProcessState.h>
#include <gui/SurfaceComposerClient.h>
#include <gui/SyncScreenCaptureListener.h>
#include <ui/DisplayState.h>

#include <cstdio>
#include <signal.h>
#include <time.h>
#include <unistd.h>

using android::base::SetProperty;
using android::gui::ScreenCaptureResults;
using android::ui::DisplayState;
using android::ui::PixelFormat;
using android::ui::Rotation;
using android::AsyncScreenCaptureListener;
using android::DisplayCaptureArgs;
using android::GraphicBuffer;
using android::IBinder;
using android::Rect;
using android::ScreenshotClient;
using android::sp;
using android::SurfaceComposerClient;
using android::SyncScreenCaptureListener;

constexpr int SCREENSHOT_INTERVAL = 1;

void updateScreenBuffer() {
    static time_t lastScreenUpdate = 0;
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    if (now.tv_sec - lastScreenUpdate < SCREENSHOT_INTERVAL) {
        ALOGV("Update skipped because interval not expired at %ld", now.tv_sec);
        return;
    }

    sp<IBinder> display = SurfaceComposerClient::getInternalDisplayToken();
    DisplayCaptureArgs captureArgs = {};
    DisplayState state = {};
    SurfaceComposerClient::getDisplayState(display, &state);

    captureArgs.displayToken = display;
    captureArgs.pixelFormat = PixelFormat::RGBA_8888;
    if (state.orientation == Rotation::Rotation0
            || state.orientation == Rotation::Rotation180) {
        captureArgs.sourceCrop = Rect(
                ALS_POS_L, ALS_POS_T,
                ALS_POS_R, ALS_POS_B);
    } else {
        captureArgs.sourceCrop = Rect(
                ALS_SCREEN_WIDTH - ALS_POS_R, ALS_SCREEN_HEIGHT - ALS_POS_B,
                ALS_SCREEN_WIDTH - ALS_POS_L, ALS_SCREEN_HEIGHT - ALS_POS_T);
    }
    captureArgs.width = ALS_POS_R - ALS_POS_L;
    captureArgs.height = ALS_POS_B - ALS_POS_T;
    captureArgs.useIdentityTransform = true;
    captureArgs.captureSecureLayers = true;

    sp<AsyncScreenCaptureListener> captureListener = new AsyncScreenCaptureListener(
        [](const ScreenCaptureResults& captureResults) {
            ALOGV("Capture results received");

            uint8_t *out;
            auto resultWidth = captureResults.buffer->getWidth();
            auto resultHeight = captureResults.buffer->getHeight();
            auto stride = captureResults.buffer->getStride();

            captureResults.buffer->lock(GraphicBuffer::USAGE_SW_READ_OFTEN, reinterpret_cast<void **>(&out));
            // we can sum this directly on linear light
            uint32_t rsum = 0, gsum = 0, bsum = 0;
            for (int y = 0; y < resultHeight; y++) {
                for (int x = 0; x < resultWidth; x++) {
                    rsum += out[y * (stride * 4) + x * 4];
                    gsum += out[y * (stride * 4) + x * 4 + 1];
                    bsum += out[y * (stride * 4) + x * 4 + 2];
                }
            }
            uint32_t max = 255 * resultWidth * resultHeight;
            SetProperty("vendor.sensors.als_correction.r", std::to_string(rsum * 0x7FFFFFFFuLL / max));
            SetProperty("vendor.sensors.als_correction.g", std::to_string(gsum * 0x7FFFFFFFuLL / max));
            SetProperty("vendor.sensors.als_correction.b", std::to_string(bsum * 0x7FFFFFFFuLL / max));
            captureResults.buffer->unlock();
        }
    , 500);

    ScreenshotClient::captureDisplay(captureArgs, captureListener);
    ALOGV("Capture started at %ld", now.tv_sec);

    lastScreenUpdate = now.tv_sec;
}

int main() {
    android::ProcessState::self()->setThreadPoolMaxThreadCount(1);
    android::ProcessState::self()->startThreadPool();

    struct sigaction action{};
    sigfillset(&action.sa_mask);

    action.sa_flags = SA_RESTART;
    action.sa_handler = [](int signal) {
        if (signal == SIGUSR1) {
            ALOGV("Signal received");
            static std::mutex updateLock;
            if (!updateLock.try_lock()) {
                ALOGV("Signal dropped due to multiple call at the same time");
                return;
            }
            updateScreenBuffer();
            updateLock.unlock();
        }
    };

    if (sigaction(SIGUSR1, &action, nullptr) == -1) {
        return -1;
    }

    SetProperty("vendor.sensors.als_correction.pid", std::to_string(getpid()));

    while (true) {
        pause();
    }

    return 0;
}
