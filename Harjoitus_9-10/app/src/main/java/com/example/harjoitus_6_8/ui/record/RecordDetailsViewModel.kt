package com.example.harjoitus_6_8.ui.record

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.model.Record
import com.example.harjoitus_6_8.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val storageService: StorageService
): ViewModel() {
    val uiState = mutableStateOf(RecordDetailsUiState())

    init {
        viewModelScope.launch {
            val itemId: String = checkNotNull(savedStateHandle[RecordDetailsDestination.itemIdArg])
            storageService.getRecord(itemId).collect {record ->
                if (record != null) {
                    uiState.value = RecordDetailsUiState(record.toRecordDetails())
                }
            }

        }
    }

    suspend fun deleteRecord() {
        storageService.delete(uiState.value.recordDetails.toRecord())
    }
}

data class RecordDetailsUiState(
    val recordDetails: RecordDetails = RecordDetails()
)