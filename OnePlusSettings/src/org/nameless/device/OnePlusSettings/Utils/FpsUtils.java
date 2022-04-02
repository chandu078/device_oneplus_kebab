/*
 * Copyright (C) 2022 The Nameless-AOSP Project
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

package org.nameless.device.OnePlusSettings.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.Services.FPSInfoService;

public class FpsUtils {

    private static final String KEY_POSITION = "device_settings_fps_position";
    private static final String KEY_COLOR = "device_settings_fps_color";
    private static final String KEY_SIZE = "device_settings_fps_text_size";

    public static boolean isFPSOverlayRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                am.getRunningServices(Integer.MAX_VALUE))
            if (FPSInfoService.class.getName().equals(service.service.getClassName()))
                return true;
        return false;
    }

    public static void setFpsService(Context context, boolean enabled) {
        Intent fpsinfo = new Intent(context, FPSInfoService.class);
        if (enabled) {
            context.startServiceAsUser(fpsinfo, UserHandle.CURRENT);
        } else {
            context.stopServiceAsUser(fpsinfo, UserHandle.CURRENT);
        }
    }

    public static void notifySettingsUpdated(Context context) {
        Intent intent = new Intent(FPSInfoService.INTENT_UPDATE_SETTINGS);
        intent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    public static int getPosition(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getInt(KEY_POSITION, 0);
    }

    public static void setPosition(Context context, int position) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putInt(KEY_POSITION, position).commit();
    }

    public static boolean isPositionChanged(Context context, int position) {
        return getPosition(context) != position;
    }

    public static int getColorIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getInt(KEY_COLOR, 0);
    }

    public static void setColorIndex(Context context, int index) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putInt(KEY_COLOR, index).commit();
    }

    public static boolean isColorChanged(Context context, int index) {
        return getColorIndex(context) != index;
    }

    public static int getSizeIndex(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getInt(KEY_SIZE, 2);
    }

    public static void setSizeIndex(Context context, int index) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putInt(KEY_SIZE, index).commit();
    }

    public static boolean isSizeChanged(Context context, int index) {
        return getSizeIndex(context) != index;
    }
}
