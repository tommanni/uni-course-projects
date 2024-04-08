package com.example.disctrack.ui.courses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * CoursesViewModel for uiState/logic for CoursesScreen
 */
@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val _coursesUiState = MutableStateFlow(CoursesUiState())
    val coursesUiState: StateFlow<CoursesUiState> = _coursesUiState

    init {
        getAllParentCourses()
    }

    // Fetches all of the courses and filters the parent courses from them
    private fun getAllParentCourses() {
        viewModelScope.launch {
            try {
                val coursesResponse = courseRepository.getAllCourses()
                // Filter out no longer existing courses and non-parent courses
                val filteredCourses = coursesResponse.courses.filter { item ->
                    item.endDate == null && item.parentId == null && item.type == "1"
                }
                _coursesUiState.value = _coursesUiState.value.copy(courses = filteredCourses)
            } catch (e: IOException) {
                Log.d("CoursesViewModel", "getAllCourses() failed")
            }
        }
    }
}

/**
 * CoursesUiState to hold ui state for CoursesScreen
 */
data class CoursesUiState(
    val courses: List<CourseListItem> = listOf(),
    val filteredCourses: List<CourseListItem> = listOf(),
)