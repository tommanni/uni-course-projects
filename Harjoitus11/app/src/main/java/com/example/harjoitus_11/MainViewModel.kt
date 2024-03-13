package com.example.harjoitus_11

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lightSensor: LightSensor,
    private val proximitySensor: ProximitySensor,
    private val accelerometerSensor: AccelerometerSensor,
    private val magnetometerSensor: MagnetometerSensor
): ViewModel() {
    var lightValue by mutableFloatStateOf(0.0f)
    var proximityValue by mutableFloatStateOf(0.0f)
    var accelerometerValues by mutableStateOf(FloatArray(3))
    var magnetometerValues by mutableStateOf(FloatArray(3))

    init {
        lightSensor.startListening()
        lightSensor.setOnSensorValuesListener { values ->
            lightValue = values[0]
        }
        proximitySensor.startListening()
        proximitySensor.setOnSensorValuesListener { values ->
            proximityValue = values[0]
        }
        accelerometerSensor.startListening()
        accelerometerSensor.setOnSensorValuesListener { values ->
            accelerometerValues[0] = values[0]
            accelerometerValues[1] = values[1]
            accelerometerValues[2] = values[2]
        }
        magnetometerSensor.startListening()
        magnetometerSensor.setOnSensorValuesListener { values ->
            magnetometerValues[0] = values[0]
            magnetometerValues[1] = values[1]
            magnetometerValues[2] = values[2]
        }
    }
}