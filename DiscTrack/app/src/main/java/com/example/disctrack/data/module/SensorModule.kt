package com.example.disctrack.data.module

import android.content.Context
import com.example.disctrack.data.sensors.AccelerometerSensor
import com.example.disctrack.data.sensors.MagneticFieldSensor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Sensor module for dependency injection with hilt
 */
@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    fun provideAccelerometerSensor(@ApplicationContext context: Context): AccelerometerSensor {
        return AccelerometerSensor(context)
    }

    @Provides
    @Singleton
    fun provideMagnetometerSensor(@ApplicationContext context: Context): MagneticFieldSensor {
        return MagneticFieldSensor(context)
    }
}