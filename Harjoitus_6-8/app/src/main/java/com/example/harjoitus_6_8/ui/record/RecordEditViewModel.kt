package com.example.harjoitus_6_8.ui.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.data.RecordRepository
import com.example.harjoitus_6_8.utils.validateDate
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecordEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val recordRepository: RecordRepository
) : ViewModel() {
    var recordUiState by mutableStateOf(RecordUiState())
        private set

    private val recordId: Int = checkNotNull(savedStateHandle[RecordEditDestination.itemIdArg])

    init {
        viewModelScope.launch {
            recordUiState = recordRepository.getRecord(recordId)
                .filterNotNull()
                .first()
                .toRecordUiState(true)
        }
    }

    fun updateUiState(recordDetails: RecordDetails) {
        recordUiState =
            RecordUiState(recordDetails = recordDetails, isEntryValid = validateInput(recordDetails))
    }

    suspend fun updateRecord() {
        if (validateInput(recordUiState.recordDetails)) {
            recordRepository.update(recordUiState.recordDetails.toRecord())
        }
    }

    private fun validateInput(uiState: RecordDetails = recordUiState.recordDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
                    && muscleGroup.isNotBlank()
                    && validateDate(date)
                    && weight > 0
                    && reps > 0
        }
    }
}