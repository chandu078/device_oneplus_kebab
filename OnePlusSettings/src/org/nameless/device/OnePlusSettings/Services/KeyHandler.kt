/*
 * Copyright (C) 2021-2022 The LineageOS Project
 * Copyright (C) 2022 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.nameless.device.OnePlusSettings.Services

import android.content.Context
import android.hardware.input.InputManager
import android.media.AudioManager
import android.os.VibrationEffect
import android.view.KeyEvent

import androidx.annotation.Keep

import com.android.internal.os.DeviceKeyHandler

import org.nameless.device.OnePlusSettings.Utils.FileUtils
import org.nameless.device.OnePlusSettings.Utils.VibrationUtils
import org.nameless.device.OnePlusSettings.Utils.VolumeUtils

@Keep
class KeyHandler(context: Context) : DeviceKeyHandler {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val inputManager = context.getSystemService(InputManager::class.java)
    private val ctx = context

    private var lastPos = POSITION_BOTTOM

    override fun handleKeyEvent(event: KeyEvent): KeyEvent? {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return event
        }

        if (inputManager.getInputDevice(event.deviceId).name != "oplus,hall_tri_state_key") {
            return event
        }

        when (FileUtils.getFileValue("/proc/tristatekey/tri_state", "0")) {
            "1" -> handleMode(POSITION_TOP)
            "2" -> handleMode(POSITION_MIDDLE)
            "3" -> handleMode(POSITION_BOTTOM)
        }

        return null
    }

    private fun handleMode(position: Int) {
        when (position) {
            POSITION_TOP -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT)
                VolumeUtils.changeMediaVolume(audioManager, ctx)
            }
            POSITION_MIDDLE -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE)
                VibrationUtils.doHapticFeedbackForEffect(ctx, VibrationEffect.EFFECT_DOUBLE_CLICK, true)
                if (lastPos == POSITION_TOP) {
                    VolumeUtils.changeMediaVolume(audioManager, ctx)
                }
            }
            POSITION_BOTTOM -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL)
                VibrationUtils.doHapticFeedbackForEffect(ctx, VibrationEffect.EFFECT_HEAVY_CLICK, true)
                if (lastPos == POSITION_TOP) {
                    VolumeUtils.changeMediaVolume(audioManager, ctx)
                }
            }
        }

        lastPos = position
    }

    companion object {
        private const val TAG = "KeyHandler"

        // Slider key positions
        private const val POSITION_TOP = 1
        private const val POSITION_MIDDLE = 2
        private const val POSITION_BOTTOM = 3
    }
}
