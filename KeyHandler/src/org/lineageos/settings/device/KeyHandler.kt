/*
 * Copyright (C) 2021 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.device

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.os.UEventObserver
import android.os.VibrationEffect
import android.os.Vibrator

class KeyHandler: Service() {
    private lateinit var audioManager: AudioManager
    private lateinit var vibrator: Vibrator

    private val alertSliderEventObserver = object : UEventObserver() {
        private val lock = Any()

        override fun onUEvent(event: UEvent) {
            synchronized(lock) {
                event.get("STATE")?.let {
                    val none = it.contains("USB=0")
                    val vibration = it.contains("HOST=0")
                    val silent = it.contains("null)=0")

                    if (none && !vibration && !silent) {
                        handleMode(POSITION_BOTTOM)
                    } else if (!none && vibration && !silent) {
                        handleMode(POSITION_MIDDLE)
                    } else if (!none && !vibration && silent) {
                        handleMode(POSITION_TOP)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        audioManager = getSystemService(AudioManager::class.java)
        vibrator = getSystemService(Vibrator::class.java)

        alertSliderEventObserver.startObserving("tri-state-key")
        alertSliderEventObserver.startObserving("tri_state_key")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleMode(position: Int) {
        when (position) {
            POSITION_TOP -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT)
            }
            POSITION_MIDDLE -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_VIBRATE)
                vibrator.vibrate(MODE_VIBRATION_EFFECT)
            }
            POSITION_BOTTOM -> {
                audioManager.setRingerModeInternal(AudioManager.RINGER_MODE_NORMAL)
                vibrator.vibrate(MODE_NORMAL_EFFECT)
            }
        }
    }

    companion object {
        private const val TAG = "KeyHandler"

        // Slider key positions
        private const val POSITION_TOP = 1
        private const val POSITION_MIDDLE = 2
        private const val POSITION_BOTTOM = 3

        // Vibration effects
        private val MODE_NORMAL_EFFECT = VibrationEffect.get(VibrationEffect.EFFECT_HEAVY_CLICK)
        private val MODE_VIBRATION_EFFECT = VibrationEffect.get(VibrationEffect.EFFECT_DOUBLE_CLICK)
    }
}
