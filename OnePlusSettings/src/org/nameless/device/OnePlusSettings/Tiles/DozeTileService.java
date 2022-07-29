/*
 * Copyright (C) 2018 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Tiles;

import android.content.Intent;
import android.service.quicksettings.TileService;

import org.nameless.device.OnePlusSettings.Doze.DozeSettingsActivity;

public class DozeTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        Intent OnePlusDoze = new Intent(this, DozeSettingsActivity.class);
        OnePlusDoze.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(OnePlusDoze);
    }
}
