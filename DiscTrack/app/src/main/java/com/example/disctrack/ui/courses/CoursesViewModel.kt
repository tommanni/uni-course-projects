package com.example.disctrack.ui.courses

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import com.example.disctrack.ui.utils.calculateDistanceMeters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * CoursesViewModel for uiState/logic for CoursesScreen
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val courseDbRepository: CourseDbRepository,
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _coursesUiState = MutableStateFlow(CoursesUiState(
        userLastKnownLocation = getLastSavedLocation())
    )
    val coursesUiState: StateFlow<CoursesUiState> = _coursesUiState

    // Flow to handle user input for searching courses
    private val searchInputFlow = MutableSharedFlow<String>()

    // Define a location callback object to handle location updates.
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // Extract the last known location from the location result
            locationResult.lastLocation?.let {
                _coursesUiState.value = _coursesUiState.value.copy(userLastKnownLocation = it)
            }
        }
    }

    init {
        getAllParentCourses()

        _coursesUiState.value = _coursesUiState.value.copy(
            userLastKnownLocation = getLastSavedLocation()
        )
        // Collect values from input flow, debounce them for 300 milliseconds using the debounce
        // operator, and call the getCoursesByNameOrLocation function with the debounced input value.
        // This reduces the amount of api calls made.
        viewModelScope.launch {
            searchInputFlow
                .debounce(300)
                .collect { value ->
                    getCoursesByNameOrLocation(value)
                }
        }
    }

    // Gets courses that are in the visible are of the Maps ui component and adds them to the state
    fun getCoursesInMapVisibleRegion(nearLeft: LatLng, farRight: LatLng) {
        viewModelScope.launch {
            val courses = _coursesUiState.value.courses
            val shownCoursesOnMap = _coursesUiState.value.shownCoursesOnMap

            val newCoursesToAdd = courses.filter { course ->
                !course.lat.isNullOrBlank() && !course.lon.isNullOrBlank() &&
                        course.lat.toDouble() >= nearLeft.latitude &&
                        course.lon.toDouble() >= nearLeft.longitude &&
                        course.lat.toDouble() <= farRight.latitude &&
                        course.lon.toDouble() <= farRight.longitude &&
                        !shownCoursesOnMap.contains(course)
            }

            if (newCoursesToAdd.isNotEmpty()) {
                _coursesUiState.value = _coursesUiState.value.copy(
                    shownCoursesOnMap = shownCoursesOnMap.toMutableList().apply { addAll(newCoursesToAdd) }
                )
            }
        }
    }

    /* TODO: Fetch courses at app startup and load them to db, get courses from db */
    // Fetches all of the courses and filters the parent courses from them
    private fun getAllParentCourses() {
        viewModelScope.launch {
            try {
                val courses = courseDbRepository.getAllCourses()
                _coursesUiState.value = _coursesUiState.value.copy(courses = courses)
                getNearbySortedCourses()
            } catch (e: IOException) {
                Log.d("CoursesViewModel", "getAllCourses() failed")
            }
        }
    }

    /*TODO: if course is type 2, no layuts -> straight to round setup, type 1 -> select layout*/

    // Emits user entered values to the searchInputFlow
    fun searchCoursesByNameOrLocation(value: String) {
        viewModelScope.launch {
            searchInputFlow.emit(value)
        }
    }

    // Fetches courses and layouts by name or location, courses searched by name come first
    private fun getCoursesByNameOrLocation(value: String) {
        viewModelScope.launch {
            try {
                // If user hasn't entered at least 3 characters, set nearby courses,
                val nearbyCourses = if (value.length < 3) {
                    _coursesUiState.value.nearbyCourses
                } else {
                    // Else fetch courses by name and join with courses by location
                    val coursesResponse = courseRepository.getCoursesByName(value)
                    // If courses are null, add empty list
                    val courses = coursesResponse.courses ?: emptyList()
                    courses + _coursesUiState.value.courses.filter { course ->
                        course.city?.lowercase()?.contains(value.lowercase()) ?: false
                    }
                }
                _coursesUiState.value = _coursesUiState.value.copy(shownCourses = nearbyCourses)
            } catch (e: IOException) {
                Log.d("CoursesViewModel", "getCoursesByNameOrLocation() failed")
            }
        }
    }

    // Fetches a course by id
    fun getCourseById(id: String) {
        viewModelScope.launch {
            try {

            } catch (e: IOException) {
                Log.d("CoursesViewModel", "getCourseById() failed")
            }
        }
    }

    // Filter and sort courses that are within a 25 kilometer radius to the user's current location
    private fun getNearbySortedCourses() {
        val userLocation = _coursesUiState.value.userLastKnownLocation
        val nearbySortedCourses = _coursesUiState.value.courses.filter { course ->
            val lat = course.lat?.toDoubleOrNull()
            val lon = course.lon?.toDoubleOrNull()
            if (lat != null && lon != null)  {
                calculateDistanceMeters(
                    userLocation.latitude, userLocation.longitude, lat, lon
                ) < 25000.0
            } else {
                false
            }
        }.sortedBy { course ->
            val lat = course.lat?.toDoubleOrNull() ?: 0.0
            val lon = course.lon?.toDoubleOrNull() ?: 0.0
            calculateDistanceMeters(userLocation.latitude, userLocation.longitude, lat, lon)
        }
        _coursesUiState.value = _coursesUiState.value.copy(
            shownCourses = nearbySortedCourses,
            nearbyCourses = nearbySortedCourses
        )
    }

    // Stop location updates
    fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
        saveLastKnownLocation()
    }

    // Start location updates if user has given location permissions
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(hasPermission: Boolean) {
        Log.d("StartLocationUpdates: ", hasPermission.toString())

        if (hasPermission) {
            locationClient.requestLocationUpdates(
                LocationRequest.Builder(10000).build(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    // Save last known user location to DataStore
    private fun saveLastKnownLocation() {
        val LATITUDE = doublePreferencesKey("lat")
        val LONGITUDE = doublePreferencesKey("lon")
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[LATITUDE] = _coursesUiState.value.userLastKnownLocation.latitude
                preferences[LONGITUDE] = _coursesUiState.value.userLastKnownLocation.longitude
            }
        }
    }

    // Get last known user location from datastore if exists, else default values
    private fun getLastSavedLocation(): Location {
        val LATITUDE = doublePreferencesKey("lat")
        val LONGITUDE = doublePreferencesKey("lon")
        val location = Location("")

        viewModelScope.launch {
            dataStore.data.first().let { preferences ->
                location.latitude = preferences[LATITUDE] ?:  63.359
                location.longitude = preferences[LONGITUDE] ?:  25.843
            }
        }

        return location
    }
}

/**
 * CoursesUiState to hold ui state for CoursesScreen
 */
data class CoursesUiState(
    val courses: List<CourseListItem> = listOf(),
    val shownCourses: List<CourseListItem> = listOf(),
    val shownCoursesOnMap: MutableList<CourseListItem> = mutableListOf(),
    val nearbyCourses: List<CourseListItem> = listOf(),
    val userLastKnownLocation: Location,
    val smallestNearLeft: LatLng = LatLng(70.037472, 30.487070),
    val biggestFarRight: LatLng = LatLng(60.023696, 19.870411),
)