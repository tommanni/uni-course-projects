package com.example.harjoitus_13_14

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
* StepSensorCounter to recognize step-trigger events from TYPE_STEP_COUNTER sensor
*/
class StepSensorCounter(
    private val context: Context
): StepCounter, SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepListener: StepListener? = null

    // Registers a step listener to receive step events from the step counter
    override fun registerListener(stepListener: StepListener): Boolean {
        this.stepListener = stepListener

        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor != null) {
            return sensorManager.registerListener(
                this@StepSensorCounter,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        return false
    }

    // Unregisters the listener
    override fun unregisterListener() {
        stepListener = null
        sensorManager?.unregisterListener(this@StepSensorCounter)
    }

    // Track sensor events
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            stepListener?.onStep(count = event.values[0].toInt())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

// StepCounter to register/unregister StepListener
interface StepCounter {
    fun registerListener(stepListener: StepListener): Boolean
    fun unregisterListener()
}

// Step listener to receive step-trigger events
interface StepListener {
    // onStep will trigger when there is a recognized step.
    fun onStep(count: Int)
}