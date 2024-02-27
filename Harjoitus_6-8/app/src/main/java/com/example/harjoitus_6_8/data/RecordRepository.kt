package com.example.harjoitus_6_8.data

import kotlinx.coroutines.flow.Flow

interface RecordRepository {

    fun getAllRecords(): Flow<List<Record>>

    fun sortByDate(): Flow<List<Record>>

    fun sortByWeight(): Flow<List<Record>>

    fun sortByName(): Flow<List<Record>>

    fun sortByMuscleGroup(): Flow<List<Record>>

    fun getRecord(id: Int): Flow<Record>

    fun getRecordHistory(name: String): Flow<List<Record>>

    suspend fun insert(record: Record)

    suspend fun delete(record: Record)

    suspend fun update(record: Record)
}