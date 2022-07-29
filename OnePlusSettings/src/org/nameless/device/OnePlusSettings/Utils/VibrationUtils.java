/*
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
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

    public static final String FILE_LEVEL = "/sys/devices/platform/soc/a8c000.i2c/i2c-6/6-005a/leds/vibrator/level";
    public static final String DEFAULT = "3";

    public static void doHapticFeedbackForDuration(Context context, int duration) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final boolean hapticEnabled = Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 1, UserHandle.USER_CURRENT) != 0;
        if (vibrator != null && hapticEnabled) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public static void doHapticFeedbackForEffect(Context context, int effect) {
        doHapticFeedbackForEffect(context, effect, false);
    }

    public static void doHapticFeedbackForEffect(Context context, int effect, boolean force) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        final boolean hapticEnabled = Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, 1, UserHandle.USER_CURRENT) != 0;
        if (vibrator != null && (force || hapticEnabled)) {
            vibrator.vibrate(VibrationEffect.createPredefined(effect));
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
