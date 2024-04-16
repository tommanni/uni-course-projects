package com.example.disctrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "played_holes")
data class PlayedHole(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: String,
    val holeNumber: String,
    val par: String,
    val throws: Int,
    val length: String,
)
