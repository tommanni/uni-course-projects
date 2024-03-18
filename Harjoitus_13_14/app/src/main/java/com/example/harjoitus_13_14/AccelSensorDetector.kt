package com.example.harjoitus_13_14

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 *  AccelSensorDetector to recognize step-trigger events from TYPE_ACCELEROMETER sensor
 */
class AccelSensorDetector(
    context: Context
): StepCounter, SensorEventListener {

    companion object {
        // Constants for accelerometer ring buffer sizes, step threshold, and delay
        private const val ACCEL_RING_SIZE = 500
        private const val VEL_RING_SIZE = 100
        private const val STEP_THRESHOLD = 40f
        private const val STEP_DELAY_NS = 250000000
    }

    // Accelerometer ring buffer and counter
    private var accelRingCounter = 0
    private val accelRingX = FloatArray(ACCEL_RING_SIZE)
    private val accelRingY = FloatArray(ACCEL_RING_SIZE)
    private val accelRingZ = FloatArray(ACCEL_RING_SIZE)

    // Velocity ring buffer and counter
    private var velRingCounter = 0
    private val velRing = FloatArray(VEL_RING_SIZE)

    // Last step time and old velocity estimate
    private var lastStepTimeNs: Long = 0
    private var oldVelocityEstimate = 0f

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var stepListener: StepListener? = null

    // Registers a step listener to receive step events from the accelerometer sensor
    override fun registerListener(stepListener: StepListener): Boolean {
        this.stepListener = stepListener

        val stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (stepDetectorSensor != null) {
            return sensorManager?.registerListener(
                /* listener = */ this@AccelSensorDetector,
                /* sensor = */ stepDetectorSensor,
                /* samplingPeriodUs = */ SensorManager.SENSOR_DELAY_FASTEST,
            ) ?: false
        }

        return false
    }

    // Unregisters the step listener
    override fun unregisterListener() {
        stepListener = null
        sensorManager?.unregisterListener(this@AccelSensorDetector)
    }
    // Track sensor events
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            updateAccel(
                timeNs = event.timestamp,
                x = event.values[0],
                y = event.values[1],
                z = event.values[2],
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /**
     * Accepts updates from the accelerometer.
     */
    private fun updateAccel(timeNs: Long, x: Float, y: Float, z: Float) {
        val currentAccel = FloatArray(3)
        currentAccel[0] = x
        currentAccel[1] = y
        currentAccel[2] = z

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0]
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1]
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2]

        val worldZ = FloatArray(3)
        worldZ[0] = SensorFusionMath.sum(accelRingX) / accelRingCounter.coerceAtMost(ACCEL_RING_SIZE)
        worldZ[1] = SensorFusionMath.sum(accelRingY) / accelRingCounter.coerceAtMost(ACCEL_RING_SIZE)
        worldZ[2] = SensorFusionMath.sum(accelRingZ) / accelRingCounter.coerceAtMost(ACCEL_RING_SIZE)

        val normalizationFactor = SensorFusionMath.norm(worldZ)
        worldZ[0] = worldZ[0] / normalizationFactor
        worldZ[1] = worldZ[1] / normalizationFactor
        worldZ[2] = worldZ[2] / normalizationFactor

        // Next step is to figure out the component of the current acceleration
        // in the direction of world_z and subtract gravity's contribution
        val currentZ = SensorFusionMath.dot(worldZ, currentAccel) - normalizationFactor
        velRingCounter++
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ

        val velocityEstimate = SensorFusionMath.sum(velRing)
        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD && timeNs - lastStepTimeNs > STEP_DELAY_NS) {
            stepListener?.onStep(1)
            lastStepTimeNs = timeNs
        }
        oldVelocityEstimate = velocityEstimate
    }

}

/**
 * A collection of matrix and vector operations used specifically for sensor
 * fusion.
 */
object SensorFusionMath {
    fun sum(array: FloatArray): Float {
        var retval = 0f
        for (i in array.indices) {
            retval += array[i]
        }
        return retval
    }

    fun norm(array: FloatArray): Float {
        var retval = 0f
        for (i in array.indices) {
            retval += array[i] * array[i]
        }
        return sqrt(retval.toDouble()).toFloat()
    }

    // Note: only works with 3D vectors.
    fun dot(a: FloatArray, b: FloatArray): Float {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]
    }
}