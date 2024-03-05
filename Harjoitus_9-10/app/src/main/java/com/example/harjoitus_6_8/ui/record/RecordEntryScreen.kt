package com.example.harjoitus_6_8.ui.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RecordEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.record_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: RecordEntryViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(RecordEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        RecordEntryBody(
            recordUiState = viewModel.recordUiState,
            onRecordValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveRecord()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun RecordEntryBody(
    recordUiState: RecordUiState,
    onRecordValueChange: (RecordDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        RecordInputForm(
            recordDetails = recordUiState.recordDetails,
            onRecordValueChange = onRecordValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = recordUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }
}

@Composable
fun RecordInputForm(
    recordDetails: RecordDetails,
    onRecordValueChange: (RecordDetails) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = recordDetails.name,
            onValueChange = { onRecordValueChange(recordDetails.copy(name = it)) },
            label = { Text(text = "Name*")},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = recordDetails.muscleGroup,
            onValueChange = { onRecordValueChange(recordDetails.copy(muscleGroup = it)) },
            label = { Text(text = "Muscle group*")},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = recordDetails.weight.toString(),
            onValueChange = { onRecordValueChange(recordDetails.copy(weight = it.toInt())) },
            label = { Text(text = "Weight*")},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = recordDetails.reps.toString(),
            onValueChange = { onRecordValueChange(recordDetails.copy(reps = it.toInt())) },
            label = { Text(text = "Reps*")},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = recordDetails.date,
            onValueChange = { onRecordValueChange(recordDetails.copy(date = it)) },
            label = { Text(text = "Date* (YYYY-MM-DD)")},
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}