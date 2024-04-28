package com.example.disctrack.data.manager

import android.hardware.SensorManager
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.platform.LocalContext
import com.example.disctrack.data.sensors.AccelerometerSensor
import com.example.disctrack.data.sensors.MagneticFieldSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class OrientationSensorManager @Inject constructor(
    private val accelerometerSensor: AccelerometerSensor,
    private val magneticFieldSensor: MagneticFieldSensor
) {
    private val _azimuthLiveData = MutableStateFlow(0F)
    val azimuthLiveData: StateFlow<Float> = _azimuthLiveData

    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    private var rotationMatrix = FloatArray(9)
    private var orientationAngles = FloatArray(3)

    fun startOrientationUpdates() {
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesChangedListener { values ->
            accelerometerReading = values.toFloatArray()
            updateOrientationAngles()
        }

        magneticFieldSensor.startListening()
        magneticFieldSensor.setOnSensorValuesChangedListener { values ->
            magnetometerReading = values.toFloatArray()
            updateOrientationAngles()
        }
    }

    fun stopOrientationUpdates() {
        accelerometerSensor.stopListening()
        magneticFieldSensor.stopListening()
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        _azimuthLiveData.value =  Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
    }

}