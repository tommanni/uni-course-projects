package com.example.disctrack.ui.home

import androidx.lifecycle.ViewModel
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RoundSetupViewModel @Inject constructor(
    val courseDbRepository: CourseDbRepository,
    val repository: CourseRepository
): ViewModel() {

    private val _roundSetupUiState = MutableStateFlow(RoundSetupUiState())
    val roundSetupUiState: StateFlow<RoundSetupUiState> = _roundSetupUiState

    init {

    }


}

data class RoundSetupUiState(
    val i: String = ""
)