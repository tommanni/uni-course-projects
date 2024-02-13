package com.example.harjoitus_4_5.data

import android.content.Context
import java.io.File



fun writeCalculationsToFile(calculations: List<String>, fileName: String, context: Context) {
    val file = File(context.filesDir, fileName)

    try {
        file.bufferedWriter().use { writer ->
            for (calculation in calculations) {
                writer.write(calculation)
                writer.newLine()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun readCalculationsFromFile(fileName: String, context: Context): List<String> {
    val file = File(context.filesDir, fileName)

    return if (file.exists()) {
        file.readLines().toMutableList()
    } else {
        mutableListOf()
    }
}
