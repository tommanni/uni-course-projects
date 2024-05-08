package com.example.disctrack.data.model

import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round

/**
 * Represents a played round of disc golf
 */
data class PlayedRound(
    val round: Round,
    val baskets: List<PlayedHole>
)