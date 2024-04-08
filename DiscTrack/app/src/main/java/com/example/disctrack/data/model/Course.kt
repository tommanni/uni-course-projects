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
 * @property ratingValue1 The first rating value of the course.
 * @property ratingResult1 The first rating result of the course.
 * @property ratingValue2 The second rating value of the course.
 * @property ratingResult2 The second rating result of the course.
 */
@Serializable
data class Course(
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
    val endDate: String? = null,
    @SerialName("RatingValue1")
    val ratingValue1: String? = null,
    @SerialName("RatingResult1")
    val ratingResult1: String? = null,
    @SerialName("RatingValue2")
    val ratingValue2: String? = null,
    @SerialName("RatingResult2")
    val ratingResult2: String? = null,
)

/**
 * Represents a single disc golf basket on a course.
 * @property number The number of the basket.
 * @property numberAlt An alternative number for the basket.
 * @property par The par value for the basket.
 * @property length The length value for the basket.
 * @property unit The unit of measurement for the length.
 * @property teeLat The latitude coordinate of the tee location.
 * @property teeLng The longitude coordinate of the tee location.
 * @property basketLat The latitude coordinate of the basket location.
 * @property basketLng The longitude coordinate of the basket location.
 */
@Serializable
data class Basket(
    @SerialName("Number")
    val number: String? = null,
    @SerialName("NumberAlt")
    val numberAlt: String? = null,
    @SerialName("Par")
    val par: String? = null,
    @SerialName("Length")
    val length: String? = null,
    @SerialName("Unit")
    val unit: String? = null,
    @SerialName("TeeLat")
    val teeLat: String? = null,
    @SerialName("TeeLng")
    val teeLng: String? = null,
    @SerialName("BasketLat")
    val basketLat: String? = null,
    @SerialName("BasketLng")
    val basketLng: String? = null,
)

/**
 * Represents the response structure for a disc golf course.
 * @property course The course data
 * @property baskets The list of baskets on the course
 */
@Serializable
data class CourseResponse(
    val course: Course,
    val baskets: List<Basket>
)