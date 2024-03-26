package com.example.harjoitus_15_16

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@SuppressLint("MissingPermission")
class MainViewModel(
    private val fileDirectory: File,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationDataUiState(lastKnownLocation = getLastLocation()))
    val uiState: StateFlow<LocationDataUiState> = _uiState

    var requestingLocationUpdates by mutableStateOf(false)
    var hasPermission by mutableStateOf(false)

    init {
        viewModelScope.launch {
            loadLocationData()
        }
    }

    fun setLastKnownLocation(location: Location) {
        _uiState.value = _uiState.value.copy(lastKnownLocation = location)
    }

    fun saveLocationData() {
        viewModelScope.launch {
            val updatedLocationData = _uiState.value.locationData.toMutableList().apply {
                add(_uiState.value.lastKnownLocation)
            }
            _uiState.value = _uiState.value.copy(locationData = updatedLocationData)
            saveLocationToFile()
        }
    }

    private fun getLastLocation(): Location {
        var lastLocation = Location("")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    lastLocation = location
                }
            }
        return lastLocation
    }

    private suspend fun saveLocationToFile() {
        withContext(Dispatchers.IO) {
            try {
                val file = File(fileDirectory, "location_data.txt")
                val lastKnownLocation = _uiState.value.lastKnownLocation
                val line = "${lastKnownLocation.latitude},${lastKnownLocation.longitude}\n"
                file.appendText(line)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadLocationData() {
        withContext(Dispatchers.IO) {
            try {
                val file = File(fileDirectory, "location_data.txt")
                if (file.exists()) {
                    val locations = mutableListOf<Location>()
                    file.readLines().forEach { line ->
                        val (lat, lon) = line.split(",")
                        val location = Location("").apply {
                            this.longitude = lon.toDouble()
                            this.latitude = lat.toDouble()
                        }
                        locations.add(location)
                    }
                    _uiState.value = _uiState.value.copy(locationData = locations)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

data class LocationDataUiState(
    val lastKnownLocation: Location,
    val locationData: MutableList<Location> = mutableListOf()
)