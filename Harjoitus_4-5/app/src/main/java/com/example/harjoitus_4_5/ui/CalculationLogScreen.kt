package com.example.harjoitus_4_5.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.harjoitus_4_5.R
import com.example.harjoitus_4_5.ui.theme.Shapes

@Composable
fun CalculationLogScreen(
    calculations: List<String>,
    onCalculatorButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        calculations.reversed().forEach { calculation ->
            Text(
                text = calculation,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small)),
                style = MaterialTheme.typography.displayMedium
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        Button(
            onClick = { onCalculatorButtonClicked() },
            shape = Shapes.small,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                text = stringResource(R.string.calculator_screen_button),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}