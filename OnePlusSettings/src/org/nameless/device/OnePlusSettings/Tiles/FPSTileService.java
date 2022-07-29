/*
 * Copyright (C) 2020 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Tiles;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.UserHandle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.nameless.device.OnePlusSettings.R;
import org.nameless.device.OnePlusSettings.Services.FPSInfoService;

public class FPSTileService extends TileService {

    private boolean isShowing = false;

    @Override
    public void onStartListening() {
        super.onStartListening();
        ActivityManager manager =
                (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FPSInfoService.class.getName().equals(
                    service.service.getClassName())) {
                isShowing = true;
            }
        }
        updateTile();
    }

    @Override
    public void onClick() {
        Intent fpsinfo = new Intent(this, FPSInfoService.class);
        if (!isShowing) {
            this.startServiceAsUser(fpsinfo, UserHandle.CURRENT);
        } else {
            this.stopServiceAsUser(fpsinfo, UserHandle.CURRENT);
        }
        isShowing = !isShowing;
        updateTile();
    }

    private void updateTile() {
        final Tile tile = getQsTile();
        tile.setState(isShowing ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setSubtitle(isShowing ?
                getString(R.string.qs_tile_status_on) :
                getString(R.string.qs_tile_status_off));
        tile.updateTile();
    }
}
