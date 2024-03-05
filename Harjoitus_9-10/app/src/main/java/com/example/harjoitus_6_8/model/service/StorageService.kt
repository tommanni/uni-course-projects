package com.example.harjoitus_6_8.model.service

import com.example.harjoitus_6_8.model.Record
import kotlinx.coroutines.flow.Flow

interface StorageService {
    fun sortByDate(): Flow<List<Record>>

    fun sortByWeight(): Flow<List<Record>>

    fun sortByName(): Flow<List<Record>>
    suspend fun getRecord(id: String): Flow<Record?>
    suspend fun insert(record: Record): String

    suspend fun delete(record: Record)

    suspend fun update(record: Record)
}