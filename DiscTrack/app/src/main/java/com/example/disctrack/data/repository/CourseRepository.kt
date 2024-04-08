package com.example.disctrack.data.repository

import com.example.disctrack.data.network.CourseApiService
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.model.CoursesResponse
import retrofit2.http.GET
import retrofit2.http.Query
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
    // Get courses by name
    override suspend fun getCoursesByName(name: String): CoursesResponse {
        return courseApiService.getCoursesByName(name)
    }
    // Get a single course by id
    override suspend fun getCourseById(id: String): CourseResponse {
        return courseApiService.getCourseById(id)
    }
}
