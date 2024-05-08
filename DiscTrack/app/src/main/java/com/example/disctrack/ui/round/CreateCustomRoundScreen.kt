package com.example.disctrack.ui.round

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination

object CreateCustomRoundDestination: NavigationDestination {
    override val route: String = "create_custom_round"
    override val titleRes: Int = R.string.custom_round_create_title

}

/**
 * Screen to create a custom round of disc golf
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRoundScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: CreateCustomRoundViewModel = hiltViewModel()
) {
    val uiState = viewModel.customRoundUiState.collectAsState()

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.custom_round_create_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back arrow")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxSize()
        ) {
            Text(text = "Course name")
            OutlinedTextField(
                value = uiState.value.courseNameValue,
                onValueChange = { viewModel.setCourseNameValue(it) },
                label = { Text("Enter a course name") },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone =  {
                        // Lose focus
                        focusManager.clearFocus(true)
                    }
                ),
                modifier = Modifier.fillMaxWidth(),

            )

            Button(
                onClick = {
                        navController.navigate(
                            "track/" + "0" + "/" + uiState.value.courseNameValue
                        )
                          },
                shape = RoundedCornerShape(8.dp),
                enabled = uiState.value.courseNameValue != "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),

            ) {
                Text("Start the round")
            }
        }
    }
}