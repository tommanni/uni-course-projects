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
import com.example.disctrack.ui.statistics.MostPlayedCourse
import com.example.disctrack.ui.statistics.ParPerformanceStats

/**
 * Course database access object
 */
@Dao
interface CourseDao {

    // COURSE QUERIES
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(course: CourseItem)

    @Update
    suspend fun update(course: CourseItem)

    @Delete
    suspend fun delete(course: CourseItem)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): MutableList<CourseItem>

    @Query("SELECT * FROM courses WHERE lower(name) LIKE '%' || :name || '%'")
    suspend fun getCoursesByName(name: String): List<CourseItem>

    @Query("DELETE from courses")
    suspend fun deleteAllCourses()

    @Query("SELECT courseId FROM rounds ORDER BY date DESC, id DESC")
    suspend fun getPlayedCourseIdList(): List<String>

    // PLAYED ROUND QUERIES
    @Query("SELECT * FROM rounds ORDER BY date DESC, id DESC")
    suspend fun getAllRounds(): List<Round>

    @Query("SELECT * FROM played_holes WHERE roundId = :roundId")
    suspend fun getRoundPlayedHoles(roundId: String): List<PlayedHole>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRound(round: Round): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayedHole(playedHole: PlayedHole)

    @Query("SELECT * FROM rounds ORDER BY date")
    suspend fun getRoundsOrderedByDate(): MutableList<Round>

    @Query(
                "SELECT " + "courseName, COUNT(*) as timesPlayed " +
                "FROM rounds " +
                "GROUP BY courseId " +
                "ORDER BY COUNT(*) " +
                "DESC LIMIT 1"
    )
    suspend fun getMostPlayedCourse(): MostPlayedCourse?

    @Query("SELECT COUNT(DISTINCT courseId) FROM rounds")
    suspend fun getAmountOfCoursesPlayed(): Long

    @Query("SELECT COUNT(*) FROM rounds")
    suspend fun getAmountOfRoundsPlayed(): Long

    @Query("SELECT COUNT(*) FROM played_holes")
    suspend fun getAmountOfHolesPlayed(): Long

    @Query("SELECT COUNT(*) FROM played_holes WHERE par - throws = 1")
    suspend fun getAmountOfBirdies(): Long

    @Query("SELECT SUM(throws) FROM played_holes")
    suspend fun getTotalAmountOfThrows(): Long

    @Query("SELECT DISTINCT strftime('%Y', date) as uniqueYears FROM rounds")
    suspend fun getYearsPlayed(): List<String>

    @Query(
        "SELECT " +
                "SUM(CASE WHEN throws = par - 2 THEN 1 ELSE 0 END) AS aces, " +
                "SUM(CASE WHEN throws = par - 1 THEN 1 ELSE 0 END) AS birdies, " +
                "SUM(CASE WHEN throws = par THEN 1 ELSE 0 END) AS pars, " +
                "SUM(CASE WHEN throws = par + 1 THEN 1 ELSE 0 END) AS bogeys, " +
                "SUM(CASE WHEN throws >= par + 2 THEN 1 ELSE 0 END) AS dblBogeys " +
                "FROM played_holes"
    )
    suspend fun getParPerformanceStatistics(): ParPerformanceStats

    @Query(
        "SELECT " +
                "SUM(CASE WHEN throws = par - 2 THEN 1 ELSE 0 END) AS aces, " +
                "SUM(CASE WHEN throws = par - 1 THEN 1 ELSE 0 END) AS birdies, " +
                "SUM(CASE WHEN throws = par THEN 1 ELSE 0 END) AS pars, " +
                "SUM(CASE WHEN throws = par + 1 THEN 1 ELSE 0 END) AS bogeys, " +
                "SUM(CASE WHEN throws >= par + 2 THEN 1 ELSE 0 END) AS dblBogeys " +
                "FROM played_holes WHERE roundId IN " +
                "(SELECT id FROM rounds WHERE date LIKE :year || '%')"
    )
    suspend fun getParPerformanceStatisticsForYear(year: String): ParPerformanceStats

    @Query(
        "SELECT " +
                "SUM(CASE WHEN throws = par - 2 THEN 1 ELSE 0 END) AS aces, " +
                "SUM(CASE WHEN throws = par - 1 THEN 1 ELSE 0 END) AS birdies, " +
                "SUM(CASE WHEN throws = par THEN 1 ELSE 0 END) AS pars, " +
                "SUM(CASE WHEN throws = par + 1 THEN 1 ELSE 0 END) AS bogeys, " +
                "SUM(CASE WHEN throws >= par + 2 THEN 1 ELSE 0 END) AS dblBogeys " +
                "FROM played_holes WHERE roundId IN " +
                "(SELECT id FROM rounds ORDER BY date DESC LIMIT :rounds)"
    )
    suspend fun getParPerformanceStatisticsForSelectedRounds(rounds: Int): ParPerformanceStats
}