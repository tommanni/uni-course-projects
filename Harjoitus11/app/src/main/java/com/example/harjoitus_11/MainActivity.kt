package com.example.harjoitus_11

import android.annotation.SuppressLint
import android.hardware.SensorManager
import android.hardware.SensorManager.AXIS_X
import android.hardware.SensorManager.AXIS_Y
import android.hardware.SensorManager.remapCoordinateSystem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.harjoitus_11.ui.theme.Harjoitus11Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Harjoitus11Theme {
                val viewModel: MainViewModel = hiltViewModel()
                val lightValue = viewModel.lightValue
                val proximityValue = viewModel.proximityValue
                val accelerometerValues = viewModel.accelerometerValues
                val magnetometerValues = viewModel.magnetometerValues

                // Rotation matrix based on current readings from accelerometer and magnetometer.
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerValues,
                    magnetometerValues
                )

                val remappedRotationAxis = FloatArray(9)
                remapCoordinateSystem(
                    rotationMatrix,
                    AXIS_X, AXIS_Y,
                    remappedRotationAxis
                )
                
                // Express the updated rotation matrix as three orientation angles.
                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val azimuth = orientationAngles[0]
                val pitch = orientationAngles[1]
                val roll = orientationAngles[2]

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar =  {
                            CenterAlignedTopAppBar(title = { Text(text = "Sensor App") })
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = it.calculateTopPadding(), horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = String.format("Light Sensor: %.2f", lightValue))
                            Text(text = String.format("Proximity Sensor: %.2f", proximityValue))
                            Text(text = String.format("Orientation: %.2f %.2f %.2f", azimuth, pitch, roll))
                        }
                    }
                }
            }
        }
    }
}