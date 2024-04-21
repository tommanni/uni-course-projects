package com.example.disctrack.ui.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination

object CreateCustomRoundDestination: NavigationDestination {
    override val route: String = "create_custom_round"
    override val titleRes: Int = R.string.custom_round_create_title

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomRoundScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var courseNameValue by remember { mutableStateOf("") }
    var basketCountValue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    // State for currently selected button
    var selectedButton by remember { mutableStateOf("") }

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
                value = courseNameValue,
                onValueChange = { courseNameValue = it },
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
            Text(
                "Number of holes",
                Modifier.padding(top = 16.dp)
            )
            Row(Modifier.fillMaxWidth()) {
                // Create buttons to select which courses to show
                val buttons = listOf("9", "18", "Other")
                buttons.forEach { button ->
                    val isSelected = button == selectedButton
                    // Adjust shape of button based on order
                    val shape = when (button) {
                        buttons.first() -> RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                        buttons.last() -> RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
                        else -> RoundedCornerShape(0.dp)
                    }
                    // If button is selected, highlight it by using filled button, else use outlined one
                    if (isSelected) {
                        Button(
                            onClick = { }, // Because button is already selected, do nothing
                            shape = shape
                        ) {
                            Text(
                                text = button
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                selectedButton = button
                                basketCountValue = if (button != "Other") button else ""
                            },
                            shape = shape
                        ) {
                            Text(
                                text = button
                            )
                        }
                    }
                }
                if (selectedButton == "Other") {
                    OutlinedTextField(
                        value = basketCountValue,
                        label = { Text("Holes") },
                        onValueChange = {
                            basketCountValue = if (it.length <= 2) it else basketCountValue
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Lose focus
                                focusManager.clearFocus(true)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = VisualTransformation.None,
                        modifier = Modifier
                            .padding(start = 8.dp)
                    )
                }

            }
            Button(
                onClick = {
                    val bundle = bundleOf(
                        "courseName" to courseNameValue,
                        "basketCount" to basketCountValue.toInt(),
                        "isCustomRound" to true
                    )
                    // TODO
                    //navController.navigate("track", bundle)
                          },
                shape = RoundedCornerShape(8.dp),
                enabled = basketCountValue != "" && courseNameValue != "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),

            ) {
                Text("Start the round")
            }
        }
    }
}