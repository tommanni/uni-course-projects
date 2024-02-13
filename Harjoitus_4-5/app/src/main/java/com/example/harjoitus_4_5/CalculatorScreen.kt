package com.example.harjoitus_4_5

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.harjoitus_4_5.data.DataSource
import com.example.harjoitus_4_5.ui.CalculationLogScreen
import com.example.harjoitus_4_5.ui.CalculationScreen
import com.example.harjoitus_4_5.ui.CalculationsViewModel
import com.example.harjoitus_4_5.ui.theme.Harjoitus_45Theme

enum class CalculatorScreen {
    Start,
    Log
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorAppBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displayLarge
        )},
        modifier = modifier
    )
}

@Composable
fun CalculatorApp(
    viewModel: CalculationsViewModel = CalculationsViewModel(LocalContext.current),
    navController: NavHostController = rememberNavController()
    ) {
        Scaffold(
            topBar = { CalculatorAppBar() }
        ) { innerPadding ->
            val uiState by viewModel.uiState.collectAsState()

            NavHost(
                navController = navController,
                startDestination = CalculatorScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = CalculatorScreen.Start.name) {
                    CalculationScreen(
                        operators = DataSource.operators,
                        viewModel = viewModel,
                        onHistoryButtonClicked = {
                            navController.navigate(CalculatorScreen.Log.name)
                        }
                    )
                }
                composable(route = CalculatorScreen.Log.name) {
                    CalculationLogScreen(
                        calculations = uiState.calculationStrings,
                        onCalculatorButtonClicked = {
                            navController.navigate(CalculatorScreen.Start.name)
                        }
                    )
                }
            }
    }
}

@Preview
@Composable
fun CalculatorAppPreview() {
    Harjoitus_45Theme {
        CalculatorApp()
    }
}