package com.example.harjoitus_6_8.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: String,
    val muscleGroup: String,
    val weight: Int,
    val reps: Int
)
