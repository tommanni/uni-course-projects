package com.example.harjoitus_11

abstract class MeasurableSensor(
    protected val sensorType: Int
) {

    protected var onSensorValuesChanged: ((List<Float>) -> Unit)? = null

    abstract val doesSensorExist: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesListener(listener: (List<Float>) -> Unit) {
        onSensorValuesChanged = listener
    }
}