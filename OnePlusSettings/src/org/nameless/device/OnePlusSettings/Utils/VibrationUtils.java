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
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.MainSettings;
import org.nameless.device.OnePlusSettings.Utils.FileUtils;

public class VibrationUtils {

    public static final String FILE_LEVEL = "/sys/devices/platform/soc/a8c000.i2c/i2c-3/3-005a/leds/vibrator/level";
    public static final String DEFAULT = "3";

    public static void doHapticFeedback(Context context, int effect) {
        doHapticFeedback(context, effect, false);
    }

    public static void doHapticFeedback(Context context, int effect, boolean force) {
        final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final boolean hapticEnabled = Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 1, UserHandle.USER_CURRENT) != 0;
        if (mVibrator != null && (force || hapticEnabled)) {
            mVibrator.vibrate(VibrationEffect.get(effect));
        }
    }

    public static int getVibStrength() {
        return Integer.parseInt(FileUtils.getFileValue(FILE_LEVEL, DEFAULT));
    }

    public static void restoreVibStrengthSetting(Context context) {
        if (FileUtils.isFileWritable(FILE_LEVEL)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            int value = sharedPrefs.getInt(MainSettings.KEY_VIBSTRENGTH, getVibStrength());
            setVibStrength(context, value);
        }
    }

    public static void setVibStrength(Context context, int value) {
        FileUtils.writeLine(FILE_LEVEL, String.valueOf(value));
    }
}
