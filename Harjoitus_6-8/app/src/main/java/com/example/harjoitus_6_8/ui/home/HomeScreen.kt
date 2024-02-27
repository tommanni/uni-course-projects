package com.example.harjoitus_6_8.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.data.Record
import com.example.harjoitus_6_8.ui.AppViewModelProvider
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToRecordEntry: () -> Unit,
    navigateToRecordUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton =  {
            FloatingActionButton(
                onClick = navigateToRecordEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.record_entry_title)
                )
            }
        }
    ) { paddingValues ->
        HomeBody(
            recordList = homeUiState.recordList,
            onItemClick = navigateToRecordUpdate,
            viewModel = viewModel,
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
    }
}

@Composable
fun SortRecordsRow(
    sortByDate: () -> Unit,
    sortByName: () -> Unit,
    sortByWeight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = "Sort by:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = { sortByDate() },
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(text = "Date")
        }
        Button(
            onClick = { sortByName() },
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(text = "Exercise")
        }
        Button(
            onClick = { sortByWeight() },
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(text = "Weight")
        }
    }
}

@Composable
fun HomeBody(
    recordList: List<Record>,
    onItemClick: (Int) -> Unit,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (recordList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_record_entries),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            SortRecordsRow(
                sortByDate = { viewModel.sortByDate() },
                sortByName = { viewModel.sortByName() },
                sortByWeight = { viewModel.sortByWeight() }
            )
            RecordList(
                recordList = recordList,
                onItemClick = { onItemClick(it.id) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun RecordList(
    recordList: List<Record>,
    onItemClick: (Record) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = recordList, key = { it.id }) {item ->
            RecordItem(
                record = item,
                modifier = modifier
                    .padding(8.dp)
                    .clickable { onItemClick(item) }
            )
        }
    }
}

@Composable
fun RecordItem(
    record: Record,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = record.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${record.weight} KG",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${record.reps} Reps",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${record.muscleGroup}"
                )
            }
        }
    }
}