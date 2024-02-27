package com.example.harjoitus_6_8.ui.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.harjoitus_6_8.data.Record
import com.example.harjoitus_6_8.data.RecordRepository
import com.example.harjoitus_6_8.utils.validateDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class RecordEntryViewModel(
    private val recordsRepository: RecordRepository
) : ViewModel() {
    var recordUiState by mutableStateOf(RecordUiState())
        private set

    fun updateUiState(recordDetails: RecordDetails) {
        recordUiState =
            RecordUiState(recordDetails = recordDetails, isEntryValid = validateInput(recordDetails))
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

    suspend fun saveRecord() {
        if (validateInput()) {
            recordsRepository.insert(recordUiState.recordDetails.toRecord())
        }
    }
}

data class RecordUiState(
    val recordDetails: RecordDetails = RecordDetails(),
    val isEntryValid: Boolean = false
)

data class RecordDetails(
    val id: Int = 0,
    val name: String = "",
    val date: String = "",
    val muscleGroup: String = "",
    val weight: Int = 0,
    val reps: Int = 0
)

fun RecordDetails.toRecord(): Record = Record(
    id = id,
    name = name,
    date = date,
    weight = weight,
    reps = reps,
    muscleGroup = muscleGroup
)

fun Record.toRecordUiState(isEntryValid: Boolean): RecordUiState = RecordUiState(
    recordDetails = this.toRecordDetails(),
    isEntryValid = isEntryValid
)

fun Record.toRecordDetails(): RecordDetails = RecordDetails(
    id = id,
    name = name,
    date = date,
    weight = weight,
    reps = reps,
    muscleGroup = muscleGroup
)
