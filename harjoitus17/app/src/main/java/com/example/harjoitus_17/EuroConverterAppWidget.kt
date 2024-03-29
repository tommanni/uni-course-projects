package com.example.harjoitus_17

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.example.harjoitus_17.ui.screen.EuroConverterViewModel

class EuroConverterAppWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent()
        }
    }

    @Composable
    private fun MyContent() {
        val viewModel = EuroConverterViewModel()
        val uiState = viewModel.uiState.collectAsState()
        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(Color.LightGray),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Conversion rates:", modifier = GlanceModifier.padding(12.dp))
            Text(text = "1 EUR = ${uiState.value.USD} USD")
            Text(text = "1 EUR = ${uiState.value.JPY} JPY")
            Text(text = "1 EUR = ${uiState.value.GBP} GBP")
        }
    }
}

