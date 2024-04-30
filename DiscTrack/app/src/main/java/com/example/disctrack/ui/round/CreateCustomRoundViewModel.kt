package com.example.disctrack.ui.round

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.disctrack.data.repository.CourseDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CreateCustomRoundViewModel(
    private val courseDbRepository: CourseDbRepository
): ViewModel() {

    private val _customRoundUiState = MutableStateFlow(CustomRoundUiState())
    val customRoundUiState: StateFlow<CustomRoundUiState> = _customRoundUiState.asStateFlow()

    fun setCourseNameValue(name: String) {
        _customRoundUiState.value = _customRoundUiState.value.copy(
            courseNameValue = name
        )
    }

    fun setBasketCountValue(count: String) {
        _customRoundUiState.value = _customRoundUiState.value.copy(
            basketCountValue = count
        )
    }
}

data class CustomRoundUiState(
    var courseNameValue: String = "",
    var basketCountValue: String = "",
)