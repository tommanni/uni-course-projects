package com.example.disctrack.ui.courses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.repository.CourseRepository
import com.example.disctrack.ui.utils.calculateDistanceMeters
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * CoursesViewModel for uiState/logic for CoursesScreen
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val _coursesUiState = MutableStateFlow(CoursesUiState())
    val coursesUiState: StateFlow<CoursesUiState> = _coursesUiState

    // Flow to handle user input for searching courses
    private val searchInputFlow = MutableSharedFlow<String>()

    init {
        getAllParentCourses()
        // Collect values from input flow, debounce them for 300 milliseconds using the debounce
        // operator, and call the getCoursesByNameOrLocation function with the debounced input value
        viewModelScope.launch {
            searchInputFlow
                .debounce(300)
                .collect { value ->
                    getCoursesByNameOrLocation(value)
                }
        }
    }

    /* TODO: Fetch courses at app startup and load them to db, get courses from db */
    // Fetches all of the courses and filters the parent courses from them
    private fun getAllParentCourses() {
        viewModelScope.launch {
            try {
                val coursesResponse = courseRepository.getAllCourses()
                // Filter out no longer existing courses and non-parent courses
                val filteredCourses = coursesResponse.courses?.filter { item ->
                    item.endDate == null && item.parentId == null
                }
                _coursesUiState.value = _coursesUiState.value.copy(courses = filteredCourses!!)
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
                // If user hasn't entered atl east 3 characters, set nearby courses,
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

    // Filter and sort courses that are in a 25 kilometer range to the user's location
    private fun getNearbySortedCourses() {
        val linnainmaa = LatLng(61.487350, 23.889460)
        val nearbySortedCourses = _coursesUiState.value.courses.filter { course ->
            val lat = course.lat?.toDoubleOrNull()
            val lon = course.lon?.toDoubleOrNull()
            if (lat != null && lon != null)  {
                calculateDistanceMeters(
                    linnainmaa.latitude, linnainmaa.longitude, lat, lon
                ) < 25000.0
            } else {
                false
            }
        }.sortedBy { course ->
            val lat = course.lat?.toDoubleOrNull() ?: 0.0
            val lon = course.lon?.toDoubleOrNull() ?: 0.0
            calculateDistanceMeters(linnainmaa.latitude, linnainmaa.longitude, lat, lon)
        }
        Log.d("getNearbyCourses size:", "${nearbySortedCourses.size}")
        _coursesUiState.value = _coursesUiState.value.copy(
            shownCourses = nearbySortedCourses,
            nearbyCourses = nearbySortedCourses
        )
    }
}

/**
 * CoursesUiState to hold ui state for CoursesScreen
 */
data class CoursesUiState(
    val courses: List<CourseListItem> = listOf(),
    val shownCourses: List<CourseListItem> = listOf(),
    val nearbyCourses: List<CourseListItem> = listOf()
)