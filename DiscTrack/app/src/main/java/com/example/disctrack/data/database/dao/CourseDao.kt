package com.example.disctrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(course: CourseItem)

    @Update
    suspend fun update(course: CourseItem)

    @Delete
    suspend fun delete(course: CourseItem)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): MutableList<CourseItem>

    @Query("DELETE from courses")
    suspend fun deleteAllCourses()

    @Query("SELECT * FROM rounds ORDER BY date DESC, id DESC")
    suspend fun getAllRounds(): List<Round>

    @Query("SELECT * FROM played_holes WHERE roundId = :roundId")
    suspend fun getRoundPlayedHoles(roundId: String): List<PlayedHole>

    @Query("SELECT courseId FROM rounds ORDER BY date DESC, id DESC")
    suspend fun getPlayedCourseIdList(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRound(round: Round): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayedHole(playedHole: PlayedHole)

    @Query("SELECT * FROM rounds ORDER BY date")
    suspend fun getRoundsOrderedByDate(): MutableList<Round>

    @Query("SELECT * FROM played_holes WHERE roundId = :roundId")
    suspend fun getPlayedHolesForRound(roundId: Int): MutableList<PlayedHole>
}