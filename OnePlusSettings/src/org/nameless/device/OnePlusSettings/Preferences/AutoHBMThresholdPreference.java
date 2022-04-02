/*
 * Copyright (C) 2016 The OmniROM Project
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
