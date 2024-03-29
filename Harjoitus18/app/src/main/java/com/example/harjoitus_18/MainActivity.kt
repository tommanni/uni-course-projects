package com.example.harjoitus_18

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.harjoitus_18.ui.theme.Harjoitus18Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Harjoitus18Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherAppTopBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Weather App") },
        modifier = modifier
    )
}

@Composable
fun WeatherApp(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = MainViewModel()
) {
    Scaffold(
        topBar = { WeatherAppTopBar() }
    ) { paddingValues ->
        WeatherAppBody(
            viewModel,
            modifier.padding(paddingValues)
        )
    }
}

@Composable
fun WeatherAppBody(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uistate.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.padding(vertical = 16.dp),
            value = uiState.currentPlace,
            onValueChange = { viewModel.setCurrentPlace(it) },
            label = { Text(text = "Enter place" ) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        OutlinedButton(onClick = { viewModel.getWeatherDataFromApi() }) {
            Text(text = "Submit")
        }
        Text(
            text = "Last 5 temperatures (°C):",
            modifier = modifier.padding(top = 16.dp)
        )
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiState.lastFiveTemperatures.map { (time, temperature) ->
                Text(text = "Time: $time, temperature: $temperature")
            }
        }
    }
}