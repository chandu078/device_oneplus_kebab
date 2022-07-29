/*
 * Copyright (C) 2020 Havoc-OS
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Preferences;

import org.nameless.device.OnePlusSettings.Utils.VibrationUtils;

import android.content.Context;
import android.os.VibrationEffect;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.R;

public class SwitchPreference extends androidx.preference.SwitchPreference {

    private final Context mContext;

    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context,
                androidx.preference.R.attr.switchPreferenceStyle,
                android.R.attr.switchPreferenceStyle));
    }

    public SwitchPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void performClick(View view) {
        super.performClick(view);
        VibrationUtils.doHapticFeedbackForEffect(mContext, VibrationEffect.EFFECT_CLICK);
    }
}
