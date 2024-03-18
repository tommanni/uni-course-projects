package com.example.harjoitus_13_14

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.ActivityCompat

/**
 * tepSensorDetector to recognize step-trigger events from TYPE_STEP_DETECTOR sensor
 */
class StepSensorDetector(
    private val context: Context
): StepDetector, SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepListener: StepListener? = null

    // Registers a step listener to receive step events from the step detector
    override fun registerListener(stepListener: StepListener): Boolean {
        this.stepListener = stepListener

        val hasPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return false
        }

        val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        if (stepDetectorSensor != null) {
            return sensorManager.registerListener(
                this@StepSensorDetector,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        return false
    }

    // Unregisters the listener
    override fun unregisterListener() {
        stepListener = null
        sensorManager?.unregisterListener(this@StepSensorDetector)
    }

    // Track sensor events
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            stepListener?.onStep(count = event.values.size)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

// StepDetector to register/unregister StepListener
interface StepDetector {
    fun registerListener(stepListener: StepListener): Boolean
    fun unregisterListener()
}