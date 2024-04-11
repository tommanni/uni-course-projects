package com.example.disctrack.data.module

import android.content.Context
import com.example.disctrack.data.database.CourseItemDatabase
import com.example.disctrack.data.repository.CourseDbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Database module for dependency injection with Hilt
 */
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideCourseDbRepository(@ApplicationContext context: Context): CourseDbRepository {
        return CourseDbRepository(CourseItemDatabase.getDatabase(context).courseDao())
    }
}
