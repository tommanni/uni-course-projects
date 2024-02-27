package com.example.harjoitus_6_8.ui.record

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.data.Record
import com.example.harjoitus_6_8.ui.AppViewModelProvider
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RecordDetailsDestination : NavigationDestination {
    override val route = "record_details"
    override val titleRes = R.string.details_screen
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailsScreen(
    navigateToEditRecord: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(RecordDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditRecord(uiState.value.recordDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Record"
                )
            }
        }
    ) { innerPadding ->
        RecordDetailsBody(
            recordDetailsUiState = uiState.value,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteRecord()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@ExperimentalMaterial3Api
@Composable
fun RecordDetailsBody(
    recordDetailsUiState: RecordDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        RecordDetails(
            record = recordDetailsUiState.recordDetails.toRecord(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete")
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun RecordDetails(
    record: Record,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecordDetailsRow(
                label = "Movement",
                recordDetail = record.name,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RecordDetailsRow(
                label = "Date",
                recordDetail = record.date,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RecordDetailsRow(
                label = "Weight",
                recordDetail = record.weight.toString(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RecordDetailsRow(
                label = "Reps",
                recordDetail = record.reps.toString(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            RecordDetailsRow(
                label = "Muscle group",
                recordDetail = record.muscleGroup,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun RecordDetailsRow(
    label: String,
    recordDetail: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = label)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = recordDetail, fontWeight = FontWeight.Bold)
    }
}

@ExperimentalMaterial3Api
@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text("Attention") },
        text = { Text("Are you sure you want to delete?") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = "No")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = "Yes")
            }
        })
}