package com.example.disctrack.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val courseDbRepository: CourseDbRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    init {
        getPlayedRounds()
    }

    private fun getPlayedRounds() {
        viewModelScope.launch {
            val playedRounds = courseDbRepository.getAllRounds()
            _homeUiState.value = _homeUiState.value.copy(playedRounds = playedRounds)
        }
    }
}

data class HomeUiState(
    val playedRounds: List<PlayedRound> = listOf()
)