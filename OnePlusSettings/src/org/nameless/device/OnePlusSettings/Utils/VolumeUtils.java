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

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRouter;
import android.os.UserHandle;
import android.provider.Settings;

import org.nameless.device.OnePlusSettings.MainSettings;

public class VolumeUtils {

    private static final int NO_CHANGE_VOLUME = 0;
    private static final int MUTE_VOLUME = 1;
    private static final int RESTORE_VOLUME = 2;

    private static final String KEY_VOLUME = "device_settings_saved_volume";

    private static boolean isSilentMode(AudioManager audioManager) {
        return audioManager.getRingerModeInternal() == audioManager.RINGER_MODE_SILENT;
    }

    private static boolean isSpeakerOutput(Context context) {
        MediaRouter mr = (MediaRouter) context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo ri = mr.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_AUDIO);
        String speakerOutput = context.getResources().getString(
                com.android.internal.R.string.default_audio_route_name);
        return ri.getName().equals(speakerOutput);
    }

    private static void saveVolume(Context context, int volume) {
        Settings.System.putIntForUser(context.getContentResolver(),
                KEY_VOLUME, volume, UserHandle.USER_CURRENT);
    }

    private static int getSavedVolume(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(),
                KEY_VOLUME, 0, UserHandle.USER_CURRENT);
    }

    private static int getMediaVolume(AudioManager audioManager) {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private static void setMediaVolume(AudioManager audioManager, int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    private static int shouldChangeMediaVolume(boolean isCurrentlyEnabled,
            boolean isSpeakerOutput, int currentVolume,
            int savedVolume, boolean isSilentMode) {
        if (isCurrentlyEnabled) {
            if (!isSpeakerOutput) {
                return NO_CHANGE_VOLUME;
            } else if (isSilentMode) {
                return currentVolume != 0 ? MUTE_VOLUME : NO_CHANGE_VOLUME;
            } else {
                return (currentVolume == 0 && savedVolume != 0) ? RESTORE_VOLUME : NO_CHANGE_VOLUME;
            }
        }
        return (currentVolume == 0 && savedVolume != 0 && isSilentMode) ? RESTORE_VOLUME : NO_CHANGE_VOLUME;
    }

    public static boolean isCurrentlyEnabled(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(),
                MainSettings.KEY_MUTE_MEDIA, 0, UserHandle.USER_CURRENT) == 1;
    }

    public static void setEnabled(Context context, boolean enabled) {
        Settings.System.putIntForUser(context.getContentResolver(),
                MainSettings.KEY_MUTE_MEDIA, enabled ? 1 : 0, UserHandle.USER_CURRENT);
        AudioManager audioManager = context.getSystemService(AudioManager.class);
        changeMediaVolume(audioManager, context);
    }

    public static void changeMediaVolume(AudioManager audioManager, Context context) {
        final int currentVolume = getMediaVolume(audioManager);
        final int savedVolume = getSavedVolume(context);
        final int shouldChange = shouldChangeMediaVolume(isCurrentlyEnabled(context),
                isSpeakerOutput(context), currentVolume,
                savedVolume, isSilentMode(audioManager));
        if (shouldChange == MUTE_VOLUME) {
            saveVolume(context, currentVolume);
            setMediaVolume(audioManager, 0);
        } else if (shouldChange == RESTORE_VOLUME) {
            setMediaVolume(audioManager, savedVolume);
            saveVolume(context, 0);
        }
    }
}
