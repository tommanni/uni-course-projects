package com.example.harjoitus_6_8.ui.record

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.data.RecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RecordDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val recordsRepository: RecordRepository
): ViewModel() {
    private val itemId: Int = checkNotNull(savedStateHandle[RecordDetailsDestination.itemIdArg])

    val uiState: StateFlow<RecordDetailsUiState> =
        recordsRepository.getRecord(itemId)
            .filterNotNull()
            .map {
                RecordDetailsUiState(recordDetails = it.toRecordDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RecordDetailsUiState()
            )

    suspend fun deleteRecord() {
        recordsRepository.delete(uiState.value.recordDetails.toRecord())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5000L
    }
}

data class RecordDetailsUiState(
    val recordDetails: RecordDetails = RecordDetails()
)