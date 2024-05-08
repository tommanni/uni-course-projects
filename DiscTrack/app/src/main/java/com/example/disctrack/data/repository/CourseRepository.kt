package com.example.disctrack.data.repository

import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.model.CoursesResponse
import com.example.disctrack.data.network.CourseApiService
import java.util.Locale
import javax.inject.Inject

/**
 * Repository to separate course data access and logic from the rest of the app
 */
interface CourseRepository {
    suspend fun getAllCourses(): CoursesResponse

    suspend fun getCoursesByName(name: String): CoursesResponse

    suspend fun getCourseById(id: String): CourseResponse
}

class NetworkCourseRepository @Inject constructor(
    private val courseApiService: CourseApiService
) : CourseRepository {
    // Get all courses located in Finland from api
    override suspend fun getAllCourses(): CoursesResponse = courseApiService.getAllCourses()
    // Get courses by name, add % to get courses whose name contains name parameter
    override suspend fun getCoursesByName(name: String): CoursesResponse {
        return courseApiService.getCoursesByName("${name.lowercase(Locale.ROOT)}%")
    }
    // Get a single course by id
    override suspend fun getCourseById(id: String): CourseResponse {
        return courseApiService.getCourseById(id)
    }
}
