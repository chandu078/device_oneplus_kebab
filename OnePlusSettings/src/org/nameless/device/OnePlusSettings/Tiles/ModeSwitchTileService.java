/*
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Tiles;

import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.ModeSwitch;
import org.nameless.device.OnePlusSettings.R;

public abstract class ModeSwitchTileService extends TileService {

    protected abstract ModeSwitch getModeSwitch();
    protected abstract String getKey();
    protected abstract Icon getIcon();
    protected abstract boolean isSupported();

    protected boolean enabled;

    public void onStartListeningExt() {}

    @Override
    public void onStartListening() {
        super.onStartListening();
        enabled = getModeSwitch().isCurrentlyEnabled();
        onStartListeningExt();
        final Tile tile = getQsTile();
        tile.setIcon(getIcon());
        tile.setState(isSupported() ? (enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE) : Tile.STATE_UNAVAILABLE);
        tile.setSubtitle(enabled ?
                getString(R.string.qs_tile_status_on) :
                getString(R.string.qs_tile_status_off));
        tile.updateTile();
    }

    public void onClickExt() {}

    @Override
    public void onClick() {
        super.onClick();
        enabled = getModeSwitch().isCurrentlyEnabled();
        getModeSwitch().setEnabled(!enabled);
        PreferenceManager.getDefaultSharedPreferences(this).edit().
                putBoolean(getKey(), !enabled).commit();
        onClickExt();
        final Tile tile = getQsTile();
        tile.setIcon(getIcon());
        tile.setState(isSupported() ? (!enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE) : Tile.STATE_UNAVAILABLE);
        tile.setSubtitle(!enabled ?
                getString(R.string.qs_tile_status_on) :
                getString(R.string.qs_tile_status_off));
        tile.updateTile();
    }
}
