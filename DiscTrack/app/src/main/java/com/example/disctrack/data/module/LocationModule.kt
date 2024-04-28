package com.example.disctrack.data.module

import android.content.Context
import com.example.disctrack.data.manager.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Location module for dependency injection with hilt
 */
@Module
@InstallIn(ViewModelComponent::class)
class LocationModule {
    @Provides
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager {
        return LocationManager(context)
    }
}