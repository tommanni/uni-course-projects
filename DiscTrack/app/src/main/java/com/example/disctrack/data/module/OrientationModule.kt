package com.example.disctrack.data.module

import android.content.Context
import com.example.disctrack.data.manager.OrientationSensorManager
import com.example.disctrack.data.sensors.AccelerometerSensor
import com.example.disctrack.data.sensors.MagneticFieldSensor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * OrientationModule for dependency injection with Hilt
 */
@Module
@InstallIn(ViewModelComponent::class)
class OrientationModule {
    @Provides
    fun provideOrientationSensorManager(
        @ApplicationContext context: Context
    ): OrientationSensorManager {
        return OrientationSensorManager(
            AccelerometerSensor(context),
            MagneticFieldSensor(context)
        )
    }
}