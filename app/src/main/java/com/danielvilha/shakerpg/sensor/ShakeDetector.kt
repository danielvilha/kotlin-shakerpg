package com.danielvilha.shakerpg.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.sqrt

enum class ShakeSensitivity(val label: String, val threshold: Float) {
    LOW("Low (Hard Shake)", 16.5f),
    MEDIUM("Standard (Recommended)", 12.5f),
    HIGH("High (Gentle Shake)", 9.0f)
}

class ShakeDetector(
    context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var sensitivity: ShakeSensitivity = ShakeSensitivity.MEDIUM
    var isEnabled: Boolean = true

    private var lastShakeTimestamp: Long = 0L
    private val debounceCooldownMs: Long = 850L

    private val _currentShakeIntensity = MutableStateFlow(0f)
    val currentShakeIntensity: StateFlow<Float> = _currentShakeIntensity.asStateFlow()

    private var isListening = false

    fun startListening() {
        if (!isListening && accelerometer != null && isEnabled) {
            sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            sensorManager?.unregisterListener(this)
            isListening = false
            _currentShakeIntensity.value = 0f
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Vector magnitude calculation
        val totalMagnitude = sqrt(x * x + y * y + z * z)
        // Delta acceleration excluding Earth's gravity
        val netAcceleration = max(0f, totalMagnitude - SensorManager.GRAVITY_EARTH)

        // Normalize intensity for UI feedback (0.0 to 1.0)
        val normalized = (netAcceleration / (sensitivity.threshold * 1.5f)).coerceIn(0f, 1f)
        _currentShakeIntensity.value = normalized

        val now = System.currentTimeMillis()
        if (netAcceleration >= sensitivity.threshold) {
            if (now - lastShakeTimestamp >= debounceCooldownMs) {
                lastShakeTimestamp = now
                onShakeDetected()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for accelerometer shake detection
    }
}
