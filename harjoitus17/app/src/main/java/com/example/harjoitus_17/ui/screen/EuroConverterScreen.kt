package com.example.harjoitus_17.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EuroConverterTopBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Currency App") },
        modifier = modifier
    )
}

@Composable
fun EuroConverterScreen(
    modifier: Modifier = Modifier,
    viewModel: EuroConverterViewModel = EuroConverterViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { EuroConverterTopBar() },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        EuroConverterBody(
            modifier.padding(paddingValues),
            uiState,
            viewModel::setCurrentEuros,
            viewModel::calculateCurrencies
        )
    }
}

@Composable
fun EuroConverterBody(
    modifier: Modifier = Modifier,
    uiState: EuroConverterState,
    setCurrentEuros: (Double) -> Unit,
    calculateCurrencies: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.padding(vertical = 16.dp),
            value = uiState.currentEuros.takeIf { it != 0.0 }?.toString() ?: "",
            onValueChange = { newValue ->
                if (newValue.isNotEmpty() && !newValue.contains(',')) {
                    setCurrentEuros(newValue.toDouble())
                }
            },
            label = { Text(text = "Enter euros") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedButton(onClick = { calculateCurrencies() }) {
            Text(text = "Calculate")
        }
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "USD (U.S. dollar) = ${String.format("%.2f", uiState.eurosAsUSD)}")
            Text(text = "JPY (Japanese yen) = ${String.format("%.2f", uiState.eurosAsJPY)}")
            Text(text = "GBP (Pound sterling) = ${String.format("%.2f", uiState.eurosAsGBP)}")
        }
    }
}
