package com.example.disctrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.disctrack.data.model.CourseListItem

/**
 * Represents a course in courses database table
 */
@Entity(tableName = "courses")
data class CourseItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseId: String,
    val parentId: String,
    val name: String,
    val fullName: String,
    val type: String,
    val countryCode: String,
    val area: String,
    val city: String,
    val location: String,
    val lat: String,
    val lon: String,
    val endDate: String
)

/**
 * Extension function to convert a CourseListItem object to a CourseItem object.
 */
fun CourseListItem.toCourseItem(): CourseItem {
    return CourseItem(
        courseId = id ?: "",
        parentId = parentId ?: "",
        name = name ?: "",
        fullName = fullName ?: "",
        type = type ?: "",
        countryCode = countryCode ?: "",
        area = area ?: "",
        city = city ?: "",
        location = location ?: "",
        lat = lat ?: "",
        lon = lon ?: "",
        endDate = endDate ?: ""
    )
}

/**
 * Extension function to convert a CourseItem object to a CourseListItem object.
 */
fun CourseItem.toCourseListItem(): CourseListItem {
    return CourseListItem(
        id = courseId,
        parentId = parentId,
        name = name,
        fullName = fullName,
        type = type,
        countryCode = countryCode,
        area = area,
        city = city,
        location = location,
        lat = lat,
        lon = lon,
        endDate = endDate
    )
}