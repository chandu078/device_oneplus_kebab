/*
 * Copyright (C) 2017 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings;

import android.app.Fragment;
import android.os.Bundle;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.collapsingtoolbar.R;

public class MainSettingsActivity extends CollapsingToolbarBaseActivity {

    private MainSettings mMainSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null) {
            mMainSettingsFragment = new MainSettings();
            getFragmentManager().beginTransaction()
                .add(R.id.content_frame, mMainSettingsFragment)
                .commit();
        } else {
            mMainSettingsFragment = (MainSettings) fragment;
        }
    }
}
