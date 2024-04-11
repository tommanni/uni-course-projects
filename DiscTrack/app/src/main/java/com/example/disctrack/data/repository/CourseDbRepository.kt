package com.example.disctrack.data.repository

import com.example.disctrack.data.database.dao.CourseDao
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.database.entities.toCourseListItem
import com.example.disctrack.data.model.CourseListItem

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

}