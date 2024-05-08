package com.example.disctrack.ui.round

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel to handle CreateCustomRoundScreen logic
 */
class CreateCustomRoundViewModel: ViewModel() {

    private val _customRoundUiState = MutableStateFlow(CustomRoundUiState())
    val customRoundUiState: StateFlow<CustomRoundUiState> = _customRoundUiState.asStateFlow()

    fun setCourseNameValue(name: String) {
        _customRoundUiState.value = _customRoundUiState.value.copy(
            courseNameValue = name
        )
    }
}

data class CustomRoundUiState(
    var courseNameValue: String = "",
    var basketCountValue: String = ""
)