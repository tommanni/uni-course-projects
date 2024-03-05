package com.example.harjoitus_6_8.ui.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.model.Record
import com.example.harjoitus_6_8.model.service.StorageService
import com.example.harjoitus_6_8.utils.validateDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageService: StorageService
) : ViewModel() {
    var recordUiState by mutableStateOf(RecordUiState())
        private set

    private val recordId: String = checkNotNull(savedStateHandle[RecordEditDestination.itemIdArg])

    init {
        viewModelScope.launch {
            storageService.getRecord(recordId).collect { record ->
                if (record != null) {
                    recordUiState = RecordUiState(record.toRecordDetails())
                } else {
                    recordUiState = RecordUiState()
                }
            }
        }
    }

    fun updateUiState(recordDetails: RecordDetails) {
        recordUiState =
            RecordUiState(recordDetails = recordDetails, isEntryValid = validateInput(recordDetails))
    }

    suspend fun updateRecord() {
        if (validateInput(recordUiState.recordDetails)) {
            storageService.update(recordUiState.recordDetails.toRecord())
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