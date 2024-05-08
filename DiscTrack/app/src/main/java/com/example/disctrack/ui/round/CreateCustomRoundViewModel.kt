package com.example.disctrack.ui.round

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.repository.CourseDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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