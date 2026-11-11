package com.danielvilha.shakerpg.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural synthesizer for dice tumbling and settling clatters.
 * Generates crisp physical acoustic dice sounds without requiring external audio asset files.
 */
class DiceAudioManager {

    var isEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.Default)

    private val sampleRate = 22050
    private val tickSoundPcm: ShortArray by lazy { generateDiceClick(durationMs = 28, frequency = 1200f) }
    private val settleSoundPcm: ShortArray by lazy { generateDiceSettle(durationMs = 70, frequency = 650f) }
    private val critSoundPcm: ShortArray by lazy { generateFanfareChime(durationMs = 140, baseFreq = 587f) }

    private fun generateDiceClick(durationMs: Int, frequency: Float): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        val rng = Random(42)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 120.0) // rapid exponential decay
            val tone = sin(2.0 * Math.PI * frequency * t) * 0.4
            val noise = (rng.nextDouble() * 2.0 - 1.0) * 0.6
            val sample = ((tone + noise) * decay * Short.MAX_VALUE * 0.5).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateDiceSettle(durationMs: Int, frequency: Float): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)
        val rng = Random(101)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 60.0)
            val resonance = sin(2.0 * Math.PI * frequency * t) * 0.5 + sin(2.0 * Math.PI * (frequency * 1.6) * t) * 0.25
            val noise = (rng.nextDouble() * 2.0 - 1.0) * 0.25
            val sample = ((resonance + noise) * decay * Short.MAX_VALUE * 0.7).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateFanfareChime(durationMs: Int, baseFreq: Float): ShortArray {
        val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val decay = exp(-t * 18.0)
            // Major triad harmonics (Root, 5th, Octave)
            val tone = (sin(2.0 * Math.PI * baseFreq * t) +
                    sin(2.0 * Math.PI * (baseFreq * 1.5) * t) * 0.6 +
                    sin(2.0 * Math.PI * (baseFreq * 2.0) * t) * 0.4) / 2.0
            val sample = (tone * decay * Short.MAX_VALUE * 0.6).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun playPcm(buffer: ShortArray) {
        if (!isEnabled) return
        scope.launch {
            try {
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Auto release track after duration
                kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 50L)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Graceful fallback if device audio focus is restricted
            }
        }
    }

    fun playTumbleClick() {
        playPcm(tickSoundPcm)
    }

    fun playSettleSound(isCritSuccess: Boolean) {
        if (isCritSuccess) {
            playPcm(critSoundPcm)
        } else {
            playPcm(settleSoundPcm)
        }
    }
}
