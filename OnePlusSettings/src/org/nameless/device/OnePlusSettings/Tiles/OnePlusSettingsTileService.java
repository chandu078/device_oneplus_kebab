/*
 * Copyright (C) 2018 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Tiles;

import org.nameless.device.OnePlusSettings.MainSettingsActivity;

import android.content.Intent;
import android.service.quicksettings.TileService;

public class OnePlusSettingsTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Intent mainSettings = new Intent(this, MainSettingsActivity.class);
        mainSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(mainSettings);
    }
}
