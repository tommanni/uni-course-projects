package com.example.harjoitus_4_5.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harjoitus_4_5.data.CalculationUiState
import com.example.harjoitus_4_5.data.readCalculationsFromFile
import com.example.harjoitus_4_5.data.writeCalculationsToFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.update

class CalculationsViewModel(val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(CalculationUiState(calculationStrings()))
    val uiState: StateFlow<CalculationUiState> = _uiState.asStateFlow()

    private fun calculationStrings(): List<String> {
        return readCalculationsFromFile("calculations.txt", context).toMutableList()
    }

    private fun updateCalculationStrings(calculation: String) {
        _uiState.update { currentSate ->
            currentSate.copy(
                calculationStrings =
                if (currentSate.calculationStrings.size >= 10) {
                    val newList = currentSate.calculationStrings.drop(1)
                    newList + calculation
                } else {
                    currentSate.calculationStrings + calculation
                }
            )
        }
        writeCalculationsToFile(uiState.value.calculationStrings, "calculations.txt", context)
    }

    fun calculateAndLogResult(operand1: String, operand2: String, operation: String): String {
        val op1 = operand1.toDoubleOrNull()
        val op2 = operand2.toDoubleOrNull()

        if (op1 == null || op2 == null) return "Invalid input"

        val result = when (operation) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "x" -> op1 * op2
            else -> if (op2 == 0.0) 0.0 else op1 / op2
        }
        val calculationString = "$operand1 $operation $operand2 = $result"
        updateCalculationStrings(calculationString)
        return calculationString
    }
}