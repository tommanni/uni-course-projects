package com.example.disctrack.ui.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getCurrentDateFormatted(): String {
    // Get the current date
    val currentDate = LocalDate.now()

    // Define the desired date format
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Format the current date using the defined format
    return currentDate.format(formatter)
}