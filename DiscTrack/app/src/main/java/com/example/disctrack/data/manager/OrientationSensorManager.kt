package com.example.disctrack.data.manager

import android.hardware.SensorManager
import com.example.disctrack.data.sensors.AccelerometerSensor
import com.example.disctrack.data.sensors.MagneticFieldSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.abs

/**
 * OrienationSensorManager to handle device orientation logic
 */
class OrientationSensorManager @Inject constructor(
    private val accelerometerSensor: AccelerometerSensor,
    private val magneticFieldSensor: MagneticFieldSensor
) {
    // Live data for azimuth orientation value
    private val _azimuthLiveData = MutableStateFlow(0F)
    val azimuthLiveData: StateFlow<Float> = _azimuthLiveData

    // Variables for sensor readings
    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    // Variables to calculate orientation angles
    private var rotationMatrix = FloatArray(9)
    private var orientationAngles = FloatArray(3)

    // Start orientation updates
    fun startOrientationUpdates() {
        accelerometerSensor.startListening()
        // Set accelerometer reading values and update orientation angles
        accelerometerSensor.setOnSensorValuesChangedListener { values ->
            accelerometerReading = values.toFloatArray()
            updateOrientationAngles()
        }

        magneticFieldSensor.startListening()
        // Set magnetometer reading values and update orientation angles
        magneticFieldSensor.setOnSensorValuesChangedListener { values ->
            magnetometerReading = values.toFloatArray()
            updateOrientationAngles()
        }
    }

    // Stop orientation updates
    fun stopOrientationUpdates() {
        accelerometerSensor.stopListening()
        magneticFieldSensor.stopListening()
    }

    private val threshold = 45f
    private var lastAzimuth = 0f

    // Updates the orientation angles and sets azimuth to the first value from orientation angles
    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        val normalizedAzimuth = (azimuth + 360) % 360

        val diff = abs(normalizedAzimuth - lastAzimuth)

        // If azimuth differs from last value by threshold, update value
        if (diff >= threshold) {
            _azimuthLiveData.value = normalizedAzimuth
            lastAzimuth = normalizedAzimuth
        }
    }

}