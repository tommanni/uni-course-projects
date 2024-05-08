package com.example.disctrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a played hole in a round of disc golf
 */
@Entity(tableName = "played_holes")
data class PlayedHole(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roundId: Long,
    val holeNumber: String,
    val par: Int,
    val throws: Int,
    val length: String? = null,
)
