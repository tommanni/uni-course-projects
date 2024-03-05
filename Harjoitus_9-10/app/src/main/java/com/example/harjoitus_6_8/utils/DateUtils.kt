package com.example.harjoitus_6_8.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

fun validateDate(date: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false
        dateFormat.parse(date)
        true
    } catch (e: ParseException) {
        false
    }
}