package com.example.harjoitus_18

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_18.network.WeatherDataApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.Double.Companion.NaN

class MainViewModel(
    private val weatherDataApiService: WeatherDataApiService = WeatherDataApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uistate: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun setCurrentPlace(place: String) {
        _uiState.value = _uiState.value.copy(currentPlace = place)
    }

    fun getWeatherDataFromApi() {
        viewModelScope.launch {
            try {
                val response = weatherDataApiService.fetchWeatherData(
                    place = _uiState.value.currentPlace
                )
                val members = response.members

                val nonNaNValues = mutableListOf<Pair<String, Double>>()

                members?.forEach { member ->
                    member.measurements?.forEach { measurement ->
                        measurement.results?.forEach { result ->
                            if (result.measurementTimeSeries?.id?.contains("t2m") == true) {
                                result.measurementTimeSeries?.points?.forEach { point ->
                                    point.measurementTVP?.forEach { tvp ->
                                        val value = tvp.value
                                        val time = tvp.time
                                        if (value != null && !value.isNaN() && time != null) {
                                            nonNaNValues.add(Pair(time, value))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                val lastFiveNonNaNValues = nonNaNValues.takeLast(5)

                val timeTemperatureMap = mutableMapOf<String, Double>()

                lastFiveNonNaNValues.forEach { pair ->
                    timeTemperatureMap[pair.first] = pair.second
                }

                _uiState.value = _uiState.value.copy(lastFiveTemperatures = timeTemperatureMap)

            } catch (e: IOException) {
                Log.e("getWeatherDataFromApi()", e.message.toString())
            }
        }
    }
}

data class WeatherUiState(
    val currentPlace: String = "",
    val lastFiveTemperatures: Map<String, Double> = mutableMapOf()
)