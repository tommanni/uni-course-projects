package com.example.harjoitus_6_8.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {

    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var Instance: RecordDatabase? = null

        fun getDatabase(context: Context): RecordDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RecordDatabase::class.java, "record_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}