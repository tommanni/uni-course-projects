package com.example.harjoitus_6_8.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {

    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun sortByDate(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY weight DESC")
    fun sortByWeight(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY name DESC")
    fun sortByName(): Flow<List<Record>>

    @Query("SELECT * FROM records ORDER BY muscleGroup DESC")
    fun sortByMuscleGroup(): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id")
    fun getRecord(id: Int): Flow<Record>

    @Query("SELECT * FROM records WHERE name = :name ORDER BY date")
    fun getRecordHistory(name: String): Flow<List<Record>>

    @Insert
    suspend fun insert(record: Record)

    @Delete
    suspend fun delete(record: Record)

    @Update
    suspend fun update(record: Record)
}