/*
 * Copyright (C) 2018 The OmniROM Project
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

package org.nameless.device.OnePlusSettings.Tiles;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.UserHandle;

import org.nameless.device.OnePlusSettings.MainSettings;
import org.nameless.device.OnePlusSettings.ModeSwitch;
import org.nameless.device.OnePlusSettings.R;
import org.nameless.device.OnePlusSettings.Services.HBMModeService;
import org.nameless.device.OnePlusSettings.Utils.SwitchUtils;

public class HBMModeTileService extends ModeSwitchTileService {

    private ModeSwitch mSwitch = SwitchUtils.getHBMModeSwitch(this);
    private Intent mHbmIntent;

    @Override
    protected ModeSwitch getModeSwitch() {
        return mSwitch;
    }

    @Override
    protected String getKey() {
        return MainSettings.KEY_HBM_SWITCH;
    }

    @Override
    protected Icon getIcon() {
        return Icon.createWithResource(this, R.drawable.ic_hbm_mode);
    }

    @Override
    protected boolean isSupported() {
        return getModeSwitch().isSupported();
    }

    @Override
    public void onStartListeningExt() {
        if (!enabled) tryStopService();
    }

    @Override
    public void onClickExt() {
        if (!enabled) {
            mHbmIntent = new Intent(this, HBMModeService.class);
            this.startServiceAsUser(mHbmIntent, UserHandle.CURRENT);
        }
    }

    private void tryStopService() {
        if (mHbmIntent == null) return;
        this.stopServiceAsUser(mHbmIntent, UserHandle.CURRENT);
        mHbmIntent = null;
    }
}
