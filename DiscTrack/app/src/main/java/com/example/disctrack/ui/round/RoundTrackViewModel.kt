package com.example.disctrack.ui.round

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.model.Course
import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.repository.CourseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private const val TAG = "RoundTrackViewModel"
@HiltViewModel(assistedFactory = RoundTrackViewModel.RoundTrackViewModelFactory::class)
class RoundTrackViewModel @AssistedInject constructor(
    @Assisted val courseId: String,
    private val courseRepository: CourseRepository
): ViewModel() {

    @AssistedFactory
    interface RoundTrackViewModelFactory {
        fun create(courseId: String): RoundTrackViewModel
    }

    private val _roundUiState = MutableStateFlow(RoundUiState())
    val roundUiState: StateFlow<RoundUiState> = _roundUiState

    init {
        fetchCourse()
    }

    private fun fetchCourse() {
        viewModelScope.launch {
            try {
                val courseResponse = courseRepository.getCourseById(courseId)
                _roundUiState.value = _roundUiState.value.copy(course = courseResponse)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

data class RoundUiState(
    val course: CourseResponse = CourseResponse(Course())
)