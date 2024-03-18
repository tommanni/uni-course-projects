package com.example.harjoitus_13_14

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.harjoitus_13_14.ui.theme.Harjoitus_13_14Theme

const val DATA_STORE_NAME = "step_counts"
// preferencesDataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)

/**
 * An application to count steps and to keep track of daily average steps
 */
class MainActivity : ComponentActivity() {
    // Permission for ACTIVITY_RECOGNITION
    private var hasPermission by mutableStateOf(false)
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog.
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(RequestPermission()
        ) { isGranted: Boolean ->
            hasPermission = isGranted
        }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Harjoitus_13_14Theme {
                val viewModel = MainViewModel(LocalContext.current, dataStore)

                // Ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION
                )

                Scaffold(
                    topBar = { TopAppBar(title = { Text(text = "Step Counter") })}
                ) { paddingValues ->  
                    StepCounterBody(
                        viewModel = viewModel,
                        hasPermission = hasPermission,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

/**
 * Displays the body of the application
 */
@Composable
fun StepCounterBody(
    viewModel: MainViewModel,
    hasPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // If user doesn't grant permission for ACTIVITY_RECOGNITION, display a message
        if (!hasPermission) {
            Text(
                text = "Permission is not granted.",
                modifier = Modifier.padding(16.dp)
            )
        } else if (
                !viewModel.availableStepSensor &&
                !viewModel.availableAccelSensor &&
                !viewModel.availableStepDetector
            ) {
            // If user doesn't have necessary features on their device, display a message
            Text(
                text = "The required sensors are not available on your device :(",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            StepCounterCard(
                pauseSensorInput = viewModel::pauseSensorInput,
                resumeSensorInput = viewModel::resumeSensorInput,
                uiState = uiState.value,
                availableStepCounter = viewModel.availableStepSensor,
                availableStepDetector = viewModel.availableStepDetector
            )
            // Button to save the current steps and to start counting from 0
            OutlinedButton(
                onClick = { viewModel.saveSteps() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            ) {
                Text(text = "Save")
            }
            StepsHistoryColumn(
                uiState.value.stepCounts,
                viewModel.getDailyAverageSteps()
            )
        }
    }
}

/**
 * Displays the current step count, used step counting method and pause/resume button
 */
@Composable
fun StepCounterCard(
    pauseSensorInput: () -> Unit,
    resumeSensorInput: () -> Unit,
    uiState: StepCounterUiState,
    availableStepCounter: Boolean,
    availableStepDetector: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Display the used step counting method based on user's device's available features
        Text(
            text = "Using " +
                    if (availableStepCounter)
                        "Step sensor"
                    else if (availableStepDetector)
                        "Step detector"
                    else
                        "Accelerometer",
            Modifier
                .offset(16.dp, 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.steps}",
                fontSize = 80.sp
            )
            // Step counter does not allow pausing sensor input, so resume/pause icon is not
            // necessary
            if (!availableStepCounter) {
                FilledIconButton(
                    onClick = {
                        if (uiState.isPaused) resumeSensorInput() else pauseSensorInput()
                    },
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .width(100.dp)
                        .height(52.dp)
                ) {
                    // Display pause/resume button based on current state
                    if (uiState.isPaused) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start icon",
                            modifier = Modifier
                                .size(28.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.pause_icon),
                            contentDescription = "Pause icon",
                            modifier = Modifier
                                .size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays the step counting history and daily average steps
 */
@Composable
fun StepsHistoryColumn(
    stepCounts: Map<String, Any>,
    dailyAverageSteps: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = "Daily average: $dailyAverageSteps",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(stepCounts.entries.toList()) { (date, steps) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = date)
                    Text(text = "$steps")
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }
    }
}