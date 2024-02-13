package com.example.harjoitus_4_5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.harjoitus_4_5.CalculatorScreen
import com.example.harjoitus_4_5.R
import com.example.harjoitus_4_5.data.DataSource
import com.example.harjoitus_4_5.ui.theme.Harjoitus_45Theme
import com.example.harjoitus_4_5.ui.theme.Shapes

@Composable
fun CalculationScreen(
    operators: List<String>,
    viewModel: CalculationsViewModel,
    onHistoryButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        operators.forEach { operation ->
            CalculationRow(operation = operation, viewModel = viewModel)
        }
        Spacer(modifier = Modifier.fillMaxHeight().weight(1f))
        Button(
            onClick = { onHistoryButtonClicked() },
            shape = Shapes.small,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(R.string.log_screen_button_text),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CalculationRow(
    operation: String,
    viewModel: CalculationsViewModel,
    modifier: Modifier = Modifier
) {
    var operand1 by rememberSaveable { mutableStateOf("") }
    var operand2 by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    Column {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = operand1,
                onValueChange = {operand1 = it},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = operation,
                style = MaterialTheme.typography.displayMedium
            )
            TextField(
                value = operand2,
                onValueChange = {operand2 = it},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = {
                    result = viewModel.calculateAndLogResult(operand1, operand2, operation)
                    operand1 = ""
                    operand2 = ""
                },
                shape = Shapes.small,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                Text(text = stringResource(R.string.calculate_result))
            }
        }
        Text(
            text = result,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(horizontal = dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        )
        Divider(
            thickness = dimensionResource(R.dimen.divider_thickness),
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }
}