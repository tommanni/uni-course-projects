package com.example.disctrack.data.repository

import com.example.disctrack.data.database.dao.CourseDao
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.database.entities.toCourseListItem
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.ui.statistics.MostPlayedCourse
import com.example.disctrack.ui.statistics.ParPerformanceStats

/**
 * Repository that provides insert, update, delete, and retrieve of [CourseItem] from a given data source.
 */
class CourseDbRepository(private val courseDao: CourseDao) {

    // COURSE DATA
    suspend fun insert(course: CourseItem) = courseDao.insert(course)

    suspend fun update(course: CourseItem) = courseDao.update(course)

    suspend fun delete(course: CourseItem) = courseDao.delete(course)

    suspend fun getAllCourses(): MutableList<CourseListItem> {
        val courses = courseDao.getAllCourses()
        return courses.map { course ->
            course.toCourseListItem()
        }.toMutableList()
    }

    suspend fun getCoursesByName(name: String): List<CourseListItem> {
        val courses = courseDao.getCoursesByName(name)
        return courses.map { course ->
            course.toCourseListItem()
        }
    }

    suspend fun deleteAllCourses() = courseDao.deleteAllCourses()


    // PLAYED ROUND DATA
    suspend fun insertRound(round: Round): Long = courseDao.insertRound(round)

    suspend fun insertPlayedHole(playedHole: PlayedHole) = courseDao.insertPlayedHole(playedHole)

    suspend fun getPlayedCourseIdList(): List<String> = courseDao.getPlayedCourseIdList()

    suspend fun getAllRounds(): MutableList<PlayedRound> {
        return courseDao.getAllRounds().map { round ->
            PlayedRound(round, courseDao.getRoundPlayedHoles(round.id.toString()))
        }.toMutableList()
    }

    suspend fun getMostPlayedCourse(): MostPlayedCourse {
        return courseDao.getMostPlayedCourse() ?: MostPlayedCourse("-", 0)
    }

    suspend fun getAmountOfCoursesPlayed(): Long = courseDao.getAmountOfCoursesPlayed()

    suspend fun getAmountOfRoundsPlayed(): Long = courseDao.getAmountOfRoundsPlayed()

    suspend fun getAmountOfHolesPlayed(): Long = courseDao.getAmountOfHolesPlayed()

    suspend fun getTotalAmountOfThrows(): Long = courseDao.getTotalAmountOfThrows()

    suspend fun getYearsPlayed(): List<String> = courseDao.getYearsPlayed()

    suspend fun getParPerformanceStatistics(): ParPerformanceStats =
        courseDao.getParPerformanceStatistics()


    suspend fun getParPerformanceStatisticsForYear(year: String): ParPerformanceStats =
        courseDao.getParPerformanceStatisticsForYear(year)

    suspend fun getParPerformanceStatisticsForSelectedRounds(rounds: Int): ParPerformanceStats =
        courseDao.getParPerformanceStatisticsForSelectedRounds(rounds)

    suspend fun getAmountOfBirdies(): Long = courseDao.getAmountOfBirdies()
}