/*
 * Copyright (C) 2016 The OmniROM Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;

import androidx.preference.PreferenceManager;

import org.nameless.device.OnePlusSettings.MainSettings;

public class AutoHBMThresholdPreference extends CustomSeekBarPreference {

    private static int mMinVal = 0;
    private static int mMaxVal = 60000;
    private static int mDefVal = 20000;

    public AutoHBMThresholdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInterval = 1000;
        mShowSign = false;
        mUnits = "";
        mContinuousUpdates = false;
        mMinValue = mMinVal;
        mMaxValue = mMaxVal;
        mDefaultValueExists = true;
        mDefaultValue = mDefVal;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mValue = Integer.parseInt(sharedPrefs.getString(MainSettings.KEY_AUTO_HBM_THRESHOLD, "20000"));

        setPersistent(false);
    }

    @Override
    protected void changeValue(int newValue) {
        SharedPreferences.Editor prefChange = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefChange.putString(MainSettings.KEY_AUTO_HBM_THRESHOLD, String.valueOf(newValue)).commit();
    }
}
