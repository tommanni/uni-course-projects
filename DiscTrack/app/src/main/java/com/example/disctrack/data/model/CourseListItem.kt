package com.example.disctrack.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a disc golf course.
 * @property id The unique identifier of the course.
 * @property parentId The identifier of the parent course (if applicable).
 * @property name The name of the course.
 * @property fullName The full name of the course.
 * @property type The type of the course.
 * @property countryCode The country code of the course location.
 * @property area The area where the course is located.
 * @property city The city where the course is located.
 * @property location The location of the course.
 * @property lat The latitude coordinate of the course location.
 * @property lon The longitude coordinate of the course location.
 * @property endDate The end date of the course (if course/layout has been closed).
 */
@Serializable
data class CourseListItem(
    @SerialName("ID")
    val id: String? = null,
    @SerialName("ParentID")
    val parentId: String? = null,
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Fullname")
    val fullName: String? = null,
    @SerialName("Type")
    val type: String? = null,
    @SerialName("CountryCode")
    val countryCode: String? = null,
    @SerialName("Area")
    val area: String? = null,
    @SerialName("City")
    val city: String? = null,
    @SerialName("Location")
    val location: String? = null,
    @SerialName("X")
    val lat: String? = null,
    @SerialName("Y")
    val lon: String? = null,
    @SerialName("Enddate")
    val endDate: String? = null
)

/**
 * Represents the response structure for a disc golf course.
 * @property courses The list of courses
 * @property errors The list of errors occurred during api response
 */
@Serializable
data class CoursesResponse(
    val courses: List<CourseListItem>,
    @SerialName(value = "Errors")
    val errors: List<String>
)