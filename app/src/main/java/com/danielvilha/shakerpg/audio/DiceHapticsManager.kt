package com.danielvilha.shakerpg.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class DiceHapticsManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isEnabled: Boolean = true

    fun playTumbleTick() {
        if (!isEnabled || vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            vibrator.vibrate(effect)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(12, 80)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(12)
        }
    }

    fun playSettleFeedback(isCritSuccess: Boolean, isCritFail: Boolean) {
        if (!isEnabled || vibrator?.hasVibrator() != true) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when {
                isCritSuccess -> {
                    // Triumphant double beat
                    val timings = longArrayOf(0, 35, 45, 80)
                    val amplitudes = intArrayOf(0, 200, 0, 255)
                    val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                    vibrator.vibrate(effect)
                }
                isCritFail -> {
                    // Ominous heavy thud
                    val effect = VibrationEffect.createOneShot(90, 220)
                    vibrator.vibrate(effect)
                }
                else -> {
                    // Crisp settling click
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        vibrator.vibrate(effect)
                    } else {
                        val effect = VibrationEffect.createOneShot(30, 160)
                        vibrator.vibrate(effect)
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(if (isCritSuccess) 120 else 30)
        }
    }

    fun playButtonTap() {
        if (!isEnabled || vibrator?.hasVibrator() != true) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, 120))
        }
    }
}
