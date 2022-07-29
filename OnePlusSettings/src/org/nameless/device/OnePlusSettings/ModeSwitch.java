/*
 * Copyright (C) 2016 The OmniROM Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import java.util.ArrayList;

import org.nameless.device.OnePlusSettings.Utils.FileUtils;

public class ModeSwitch implements OnPreferenceChangeListener {

    public static class Node {
        public String mFile;
        public String mEnabledValue;
        public String mDisabledValue;
        public int mValueIndex;
        public boolean mDefaultValue;

        public Node(String file, String enabledValue,
                String disabledValue, boolean defaultValue) {
            mFile = file;
            mEnabledValue = enabledValue;
            mDisabledValue = disabledValue;
            mValueIndex = 0;
            mDefaultValue = defaultValue;
        }

        public Node(String file, String enabledValue,
                String disabledValue, int valueIndex, boolean defaultValue) {
            mFile = file;
            mEnabledValue = enabledValue;
            mDisabledValue = disabledValue;
            mValueIndex = valueIndex;
            mDefaultValue = defaultValue;
        }
    }

    private Context mContext;
    private Preference mPreference;
    private ArrayList<Node> mNodes;

    public ModeSwitch(Context context, Preference preference, ArrayList<Node> nodes) {
        mContext = context;
        mPreference = preference;
        mNodes = (ArrayList<Node>) nodes.clone();
    }

    public boolean isSupported() {
        for (Node node : mNodes) {
            if (!FileUtils.isFileWritable(node.mFile)) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<Node> getNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Node node : mNodes) {
            if (FileUtils.isFileWritable(node.mFile)) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public boolean isCurrentlyEnabled() {
        ArrayList<Node> nodes = getNodes();
        if (nodes.isEmpty()) {
            return false;
        }
        for (Node node : nodes) {
            if (!FileUtils.getFileValueAsBoolean(
                    node.mFile, node.mValueIndex, node.mDefaultValue)) {
                return false;
            }
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        ArrayList<Node> nodes = getNodes();
        for (Node node : nodes) {
            FileUtils.writeLine(node.mFile,
                    enabled ? node.mEnabledValue : node.mDisabledValue);
        }
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
