/*
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.MainSettings;
import org.nameless.device.OnePlusSettings.Services.AutoHBMService;

public class HBMUtils {

    private static boolean mServiceEnabled = false;

    private static void startService(Context context) {
        context.startServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
        mServiceEnabled = true;
    }

    private static void stopService(Context context) {
        mServiceEnabled = false;
        context.stopServiceAsUser(new Intent(context, AutoHBMService.class),
                UserHandle.CURRENT);
    }

    public static void enableService(Context context) {
        if (isAutoHBMEnabled(context) && !mServiceEnabled) {
            startService(context);
        } else if (!isAutoHBMEnabled(context) && mServiceEnabled) {
            stopService(context);
        }
    }

    public static boolean isAutoHBMEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainSettings.KEY_AUTO_HBM_SWITCH, false);
    }

    public static void setAutoHBMEnabled(Context context, boolean enabled) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putBoolean(MainSettings.KEY_AUTO_HBM_SWITCH, enabled).commit();
    }
}
