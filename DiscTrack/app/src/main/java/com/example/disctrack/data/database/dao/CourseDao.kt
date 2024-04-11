package com.example.disctrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.disctrack.data.database.entities.CourseItem

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(course: CourseItem)

    @Update
    suspend fun update(course: CourseItem)

    @Delete
    suspend fun delete(course: CourseItem)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): MutableList<CourseItem>

    @Query("DELETE from courses")
    suspend fun deleteAllCourses()
}