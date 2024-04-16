package com.example.disctrack.data.model

import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round

data class PlayedRound(
    val round: Round,
    val baskets: List<PlayedHole>
)