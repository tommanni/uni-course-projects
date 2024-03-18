package com.example.harjoitus_13_14

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
// MainViewModel to handle uiState and step counting logic
class MainViewModel(
    context: Context,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val stepCounter = StepSensorCounter(context)
    private val stepDetector = StepSensorDetector(context)
    private val accelDetector = AccelSensorDetector(context)

    private val _uiState = MutableStateFlow(StepCounterUiState())
    val uiState: StateFlow<StepCounterUiState> = _uiState

    init {
        // Collect Map of dates and counts and add it ui state
        viewModelScope.launch {
            getStepCountsMap().collect {
                _uiState.value = _uiState.value.copy(stepCounts = it)
            }
        }
    }

    // Check if step detector is available and register listener if it is
    val availableStepDetector =
            stepDetector.registerListener(object : StepListener {
                override fun onStep(count: Int) {
                    val currentUiState = _uiState.value
                    if (!currentUiState.isPaused) {
                        _uiState.value = currentUiState.copy(steps = currentUiState.steps + count)
                    }
                }
            })

    // if step detector isn't available, check if accelerometer sensor is available
    // and register listener if it is
    val availableAccelSensor =
        if (!availableStepDetector)
            accelDetector.registerListener(object: StepListener {
                override fun onStep(count: Int) {
                    val currentUiState = _uiState.value
                    if (!currentUiState.isPaused) {
                        _uiState.value = currentUiState.copy(steps = currentUiState.steps + count)
                    }
                }
            }) else false

    // If step detector & accel sensor are not available, check if step counter is available
    // and register listener if it is
    val availableStepSensor =
        if (!availableStepDetector && !availableAccelSensor)
            stepCounter.registerListener(object : StepListener {
                override fun onStep(count: Int) {
                    val currentUiState = _uiState.value
                    if (!currentUiState.isPaused) {
                        _uiState.value = currentUiState.copy(steps = count)
                    }
                }
            }) else false

    fun pauseSensorInput() {
        _uiState.value = _uiState.value.copy(isPaused = true)
    }

    fun resumeSensorInput() {
        _uiState.value = _uiState.value.copy(isPaused = false)
    }

    // Save step count to datastore and reset current steps to 0
    fun saveSteps() {
        val date = getCurrentDate()
        val currentSteps = _uiState.value.steps

        viewModelScope.launch {
            dataStore.edit { preferences ->
                // If date exists in datastore, update it, else add new entry
                val existingStepCount = preferences[intPreferencesKey(date)] ?: 0
                preferences[intPreferencesKey(date)] = existingStepCount + currentSteps
            }
        }

        _uiState.value = _uiState.value.copy(steps = 0)
    }
    //Calculate the average amount of steps per day, if no entries return 0
    fun getDailyAverageSteps(): Int {
        val stepCounts = _uiState.value.stepCounts
        val totalSteps = stepCounts.values.sumOf { value -> value as Int }
        val numOfDays = stepCounts.size

        if (numOfDays > 0) {
            return totalSteps/numOfDays
        }

        return 0
    }
    // Return Flow of stepCounts map
    private fun getStepCountsMap(): Flow<Map<String, Any>> {
        return dataStore.data
            .map { preferences ->
                // Transform preferences into a map of key-value pairs
                preferences.asMap().mapKeys { it.key.name }.mapValues { it.value }
            }
    }
    // Get formatted current date
    private fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}
// Ui state data class for MainViewModel
data class StepCounterUiState(
    val stepCounts: Map<String, Any> = mutableMapOf(),
    val isPaused: Boolean = false,
    val steps: Int = 0
)