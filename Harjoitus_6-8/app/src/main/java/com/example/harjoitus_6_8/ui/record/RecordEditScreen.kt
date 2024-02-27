package com.example.harjoitus_6_8.ui.record

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.ui.AppViewModelProvider
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RecordEditDestination : NavigationDestination {
    override val route = "edit"
    override val titleRes = R.string.edit_record_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecordEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(RecordEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        RecordEntryBody(
            recordUiState = viewModel.recordUiState,
            onRecordValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateRecord()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}