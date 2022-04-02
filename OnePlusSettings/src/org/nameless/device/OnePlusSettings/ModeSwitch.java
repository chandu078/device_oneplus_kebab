/*
 * Copyright (C) 2016 The OmniROM Project
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

package org.nameless.device.OnePlusSettings;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import org.nameless.device.OnePlusSettings.Utils.FileUtils;

public class ModeSwitch implements OnPreferenceChangeListener {

    private Context mContext;
    private Preference mPreference;
    private String mFile;
    private String mEnabledValue;
    private String mDisabledValue;
    private boolean mDefaultValue;

    public ModeSwitch(Context context, Preference preference, String file,
            String enabledValue, String disabledValue, boolean defaultValue) {
        mContext = context;
        mPreference = preference;
        mFile = file;
        mEnabledValue = enabledValue;
        mDisabledValue = disabledValue;
        mDefaultValue = defaultValue;
    }

    public boolean isSupported() {
        return FileUtils.isFileWritable(mFile);
    }

    public String getFile() {
        if (isSupported()) {
            return mFile;
        }
        return null;
    }

    public boolean isCurrentlyEnabled() {
        return FileUtils.getFileValueAsBoolean(getFile(), mDefaultValue);
    }

    public void setEnabled(boolean enabled) {
        FileUtils.writeLine(getFile(), enabled ? mEnabledValue : mDisabledValue);
    }

    public void extCommand(Context context, boolean enabled) {}

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPreference) {
            boolean enabled = (Boolean) newValue;
            setEnabled(enabled);
            extCommand(mContext, enabled);
            return true;
        }
        return false;
    }
}
