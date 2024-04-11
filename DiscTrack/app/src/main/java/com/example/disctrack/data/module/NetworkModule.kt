package com.example.disctrack.data.module

import com.example.disctrack.data.network.CourseApiService
import com.example.disctrack.data.repository.CourseRepository
import com.example.disctrack.data.repository.NetworkCourseRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Network module for dependency injection with hilt
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    // Base url of the courses api
    private val baseUrl =
        "https://discgolfmetrix.com/"
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideCourseApiService(retrofit: Retrofit): CourseApiService {
        return retrofit.create(CourseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkCourseRepository(courseApiService: CourseApiService): CourseRepository {
        return NetworkCourseRepository(courseApiService)
    }
}