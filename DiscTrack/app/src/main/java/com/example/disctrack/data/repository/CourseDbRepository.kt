package com.example.disctrack.data.repository

import com.example.disctrack.data.database.dao.CourseDao
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.database.entities.toCourseListItem
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.model.PlayedRound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

/**
 * Repository that provides insert, update, delete, and retrieve of [CourseItem] from a given data source.
 */
class CourseDbRepository(private val courseDao: CourseDao) {
    suspend fun insert(course: CourseItem) = courseDao.insert(course)

    suspend fun update(course: CourseItem) = courseDao.update(course)

    suspend fun delete(course: CourseItem) = courseDao.delete(course)

    suspend fun getAllCourses(): MutableList<CourseListItem> {
        val courses = courseDao.getAllCourses()
        return courses.map { course ->
            course.toCourseListItem()
        }.toMutableList()
    }

    suspend fun deleteAllCourses() = courseDao.deleteAllCourses()

    suspend fun getAllRounds(): MutableList<PlayedRound> {
        return courseDao.getAllRounds().map { round ->
            PlayedRound(round, courseDao.getRoundPlayedHoles(round.id.toString()))
        }.toMutableList()
    }


    suspend fun insertRound(round: Round): Long = courseDao.insertRound(round)

    suspend fun insertPlayedHole(playedHole: PlayedHole) = courseDao.insertPlayedHole(playedHole)

    suspend fun getPlayedCourseIdList(): List<String> = courseDao.getPlayedCourseIdList()
}