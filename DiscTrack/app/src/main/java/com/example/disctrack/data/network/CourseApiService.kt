package com.example.disctrack.data.network

import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.model.CoursesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API service for getting disc golf course data
 */
interface CourseApiService {
    // Get all courses located in Finland from api
    @GET("api.php?content=courses_list&country_code=FI")
    suspend fun getAllCourses(): CoursesResponse

    // Get courses by name
    @GET("api.php?content=courses_list&country_code=FI")
    suspend fun getCoursesByName(@Query("name") name: String): CoursesResponse

    // Get a single course by id
    @GET("api.php?content=course&code=XXX")
    suspend fun getCourseById(@Query("id") id: String): CourseResponse
}