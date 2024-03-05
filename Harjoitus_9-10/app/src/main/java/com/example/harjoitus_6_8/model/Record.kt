package com.example.harjoitus_6_8.model

import com.google.firebase.firestore.DocumentId

data class Record(
    @DocumentId val id: String = "",
    val name: String = "",
    val date: String = "",
    val muscleGroup: String = "",
    val weight: Int = 0,
    val reps: Int = 0,
    val userId: String = ""
)
