package com.example.disctrack.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.disctrack.data.database.dao.CourseDao
import com.example.disctrack.data.database.entities.CourseItem
import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [CourseItem::class, Round::class, PlayedHole::class], version = 5, exportSchema = false)
abstract class CourseItemDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {

        @Volatile
        private var Instance: CourseItemDatabase? = null

        fun getDatabase(context: Context): CourseItemDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room
                    .databaseBuilder(context, CourseItemDatabase::class.java, "course_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}