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

package org.nameless.device.OnePlusSettings.Tiles;

import android.graphics.drawable.Icon;

import org.nameless.device.OnePlusSettings.MainSettings;
import org.nameless.device.OnePlusSettings.ModeSwitch;
import org.nameless.device.OnePlusSettings.R;
import org.nameless.device.OnePlusSettings.Utils.SwitchUtils;

public class DCModeTileService extends ModeSwitchTileService {

    private ModeSwitch mSwitch = SwitchUtils.getDCModeSwitch(this);

    @Override
    protected ModeSwitch getModeSwitch() {
        return mSwitch;
    }

    @Override
    protected String getKey() {
        return MainSettings.KEY_DC_SWITCH;
    }

    @Override
    protected Icon getIcon() {
        return Icon.createWithResource(this,
                enabled ? R.drawable.ic_dimming_on : R.drawable.ic_dimming_off);
    }

    @Override
    protected boolean isSupported() {
        return getModeSwitch().isSupported();
    }
}
