package com.example.disctrack.ui.courses

import android.location.Location
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.manager.LocationManager
import com.example.disctrack.data.manager.OrientationSensorManager
import com.example.disctrack.data.model.Course
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import com.example.disctrack.data.sensors.AccelerometerSensor
import com.example.disctrack.data.sensors.MagneticFieldSensor
import com.example.disctrack.ui.utils.calculateDistanceMeters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.math.abs

/**
 * CoursesViewModel for uiState/logic for CoursesScreen
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val courseDbRepository: CourseDbRepository,
    private val locationManager: LocationManager,
    private val dataStore: DataStore<Preferences>,
    private val orientationSensorManager: OrientationSensorManager
) : ViewModel() {

    // Live location updates from location manager
    private val locationLiveData: LiveData<Location> = locationManager.locationLiveData
    // Live azimuth device orientation updates from orientation sensor manager
    private val azimuthLiveData: StateFlow<Float> = orientationSensorManager.azimuthLiveData

    private val _coursesUiState = MutableStateFlow(CoursesUiState(
        userLastKnownLocation = getLastSavedLocation())
    )
    val coursesUiState: StateFlow<CoursesUiState> = _coursesUiState

    // Flow to handle user input for searching courses
    private val searchInputFlow = MutableSharedFlow<String>()

    init {
        startLocationUpdates()

        getAllParentCourses()

        // Collect values from input flow, debounce them for 300 milliseconds and call the
        // getCoursesByNameOrLocation function with the debounced input value. Because of debounce,
        // courses are not fetched on every character typed
        viewModelScope.launch {
            searchInputFlow
                .debounce(300)
                .collect { value ->
                    getCoursesByNameOrLocation(value)
                }
        }
    }

    // Stops orientation and location updates, called when view model is destroyed
    override fun onCleared() {
        super.onCleared()
        stopOrientationUpdates()
        stopLocationUpdates()
    }

    // Gets all of the courses from DB and calls getNearbyCourses()
    private fun getAllParentCourses() {
        viewModelScope.launch {
            try {
                val courses = courseDbRepository.getAllCourses()
                _coursesUiState.value = _coursesUiState.value.copy(courses = courses)
                getNearbySortedCourses()
                getRecentCourses()
            } catch (e: IOException) {
                Log.e("CoursesViewModel", "getAllCourses() failed")
            }
        }
    }

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
                // If user hasn't entered at least 3 characters, set shown courses to nearby courses
                val shownCourses = if (value.length < 3) {
                    _coursesUiState.value.nearbyCourses
                } else {
                    courseDbRepository.getCoursesByName(value.lowercase())
                }
                _coursesUiState.value = _coursesUiState.value.copy(shownCourses = shownCourses)
            } catch (e: IOException) {
                Log.e("CoursesViewModel", "getCoursesByNameOrLocation() failed")
            }
        }
    }

    // Fetches a course by id
    fun getCourseById(id: String) {
        viewModelScope.launch {
            try {
                val response = courseRepository.getCourseById(id)
                _coursesUiState.value = _coursesUiState.value.copy(selectedCourseResponse = response)
            } catch (e: IOException) {
                Log.e("CoursesViewModel", "getCourseById() failed")
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

    // Gets recently played courses
    private fun getRecentCourses() {
        viewModelScope.launch {
            val courseIdList = courseDbRepository.getPlayedCourseIdList()
            val recentCourses = _coursesUiState.value.courses.filter { course ->
                course.id in courseIdList
            }
            _coursesUiState.value = _coursesUiState.value.copy(recentCourses = recentCourses)
        }
    }

    // Start location updates
    fun startLocationUpdates() {
        locationManager.startLocationUpdates()

        if (!locationLiveData.hasActiveObservers()) {
            // Observe location live data if no observers
            locationLiveData.observeForever { location ->
                val oldUserLocation = _coursesUiState.value.userLastKnownLocation
                _coursesUiState.value = _coursesUiState.value.copy(userLastKnownLocation = location)

                // If distance is 5000m or greater to old distance, update nearby courses
                if (calculateDistanceMeters(
                        oldUserLocation.latitude,
                        oldUserLocation.longitude,
                        location.latitude,
                        location.longitude
                    ) >= 5000) {
                    getNearbySortedCourses()
                    saveLastKnownLocation()
                }
            }
        }
    }


    // Start orientation updates and set orientation angle data to state
    fun startOrientationUpdates() {
        orientationSensorManager.startOrientationUpdates()

        // Collect and set ui state with azimuth value
        viewModelScope.launch {
            azimuthLiveData
                .collect { azimuth ->
                    _coursesUiState.value = _coursesUiState.value.copy(deviceOrientation = azimuth)
                    Log.d("orientation", azimuth.toString())
                }
        }
    }

    // Stop orientation updates
    fun stopOrientationUpdates() {
        orientationSensorManager.stopOrientationUpdates()
    }


    // Stop location updates
    fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
        saveLastKnownLocation()
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

    fun setNearbyCourses() {
        _coursesUiState.value = _coursesUiState.value.copy(
            shownCourses = _coursesUiState.value.nearbyCourses
        )
    }

    fun setRecentCourses() {
        _coursesUiState.value = _coursesUiState.value.copy(
            shownCourses = _coursesUiState.value.recentCourses
        )
    }
}

/**
 * CoursesUiState to hold ui state for CoursesScreen
 */
data class CoursesUiState(
    val courses: List<CourseListItem> = listOf(),
    val shownCourses: List<CourseListItem> = listOf(),
    val nearbyCourses: List<CourseListItem> = listOf(),
    val recentCourses: List<CourseListItem> = listOf(),
    val selectedCourseResponse: CourseResponse = CourseResponse(Course(), listOf(), listOf()),
    val userLastKnownLocation: Location,
    val deviceOrientation: Float = 0F
)