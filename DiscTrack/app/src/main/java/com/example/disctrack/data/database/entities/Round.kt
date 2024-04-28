package com.example.disctrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a played round of disc golf
 */
@Entity(tableName = "rounds")
data class Round (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: String,
    val courseName: String,
    val courseLocation: String,
    val date: String,
    val rating: Int = 0
)