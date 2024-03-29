package com.example.harjoitus_17.ui.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.harjoitus_17.EuroConverterAppWidget

class EuroConverterAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = EuroConverterAppWidget()
}