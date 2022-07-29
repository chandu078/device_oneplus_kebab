/*
 * Copyright (C) 2019 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.MainSettings;
import org.nameless.device.OnePlusSettings.ModeSwitch;
import org.nameless.device.OnePlusSettings.Utils.DozeUtils;
import org.nameless.device.OnePlusSettings.Utils.SwitchUtils;
import org.nameless.device.OnePlusSettings.Utils.VibrationUtils;
import org.nameless.device.OnePlusSettings.Utils.VolumeUtils;

public class Startup extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        boolean enabled = false;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        ModeSwitch modeSwitch;

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_MUTE_MEDIA, false);
        VolumeUtils.setEnabled(context, enabled);

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_DC_SWITCH, false);
        modeSwitch = SwitchUtils.getDCModeSwitch(context);
        if (enabled) modeSwitch.setEnabled(true);

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_HBM_SWITCH, false);
        modeSwitch = SwitchUtils.getHBMModeSwitch(context);
        if (enabled) modeSwitch.setEnabled(true);

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_FPS_INFO, false);
        if (enabled) context.startServiceAsUser(new Intent(context, FPSInfoService.class), UserHandle.CURRENT);

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_GAME_SWITCH, false);
        modeSwitch = SwitchUtils.getGameModeSwitch(context);
        if (enabled) modeSwitch.setEnabled(true);

        enabled = sharedPrefs.getBoolean(MainSettings.KEY_EDGE_TOUCH, false);
        modeSwitch = SwitchUtils.getEdgeModeSwitch(context);
        if (enabled) modeSwitch.setEnabled(true);

        DozeUtils.checkDozeService(context);
        VibrationUtils.restoreVibStrengthSetting(context);
    }
}
