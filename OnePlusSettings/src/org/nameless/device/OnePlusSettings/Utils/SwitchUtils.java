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
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;

import androidx.preference.Preference;

import org.nameless.device.OnePlusSettings.ModeSwitch;
import org.nameless.device.OnePlusSettings.Services.HBMModeService;

public class SwitchUtils {
    
    public static final String FILE_DC = "/sys/devices/platform/soc/ae00000.qcom,mdss_mdp/drm/card0/card0-DSI-1/dimlayer_bl_en";
    public static final String FILE_HBM = "/sys/devices/platform/soc/ae00000.qcom,mdss_mdp/drm/card0/card0-DSI-1/hbm";
    public static final String FILE_GAME = "/proc/touchpanel/force_game_switch_enable";
    public static final String FILE_EDGE = "/proc/touchpanel/tpedge_limit_enable";

    public static ModeSwitch getDCModeSwitch(Context context) {
        return getDCModeSwitch(context, null);
    }

    public static ModeSwitch getDCModeSwitch(Context context, Preference preference) {
        return new ModeSwitch(context, preference, FILE_DC, "1", "0", false);
    }

    public static ModeSwitch getHBMModeSwitch(Context context) {
        return getHBMModeSwitch(context, null);
    }

    public static ModeSwitch getHBMModeSwitch(Context context, Preference preference) {
        return new ModeSwitch(context, preference, FILE_HBM, "5", "0", false) {
            @Override
            public void extCommand(Context context, boolean enabled) {
                Intent hbmIntent = new Intent(context, HBMModeService.class);
                if (enabled) {
                    context.startServiceAsUser(hbmIntent, UserHandle.CURRENT);
                } else {
                    context.stopServiceAsUser(hbmIntent, UserHandle.CURRENT);
                }
            }
        };
    }

    public static ModeSwitch getGameModeSwitch(Context context) {
        return getGameModeSwitch(context, null);
    }

    public static ModeSwitch getGameModeSwitch(Context context, Preference preference) {
        return new ModeSwitch(context, preference, FILE_GAME, "1", "0", false);
    }

    public static ModeSwitch getEdgeModeSwitch(Context context) {
        return getEdgeModeSwitch(context, null);
    }

    public static ModeSwitch getEdgeModeSwitch(Context context, Preference preference) {
        return new ModeSwitch(context, preference, FILE_EDGE, "1", "0", false);
    }

    public static boolean isGamingModeSupported() {
        return !Build.DEVICE.equals("instantnoodle");
    }
}
