package com.example.harjoitus_11

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    fun provideLightSensor(app: Application): LightSensor {
        return LightSensor(app)
    }

    @Provides
    @Singleton
    fun provideProximitySensor(app: Application): ProximitySensor {
        return ProximitySensor(app)
    }

    @Provides
    @Singleton
    fun provideAccelerometerSensor(app: Application): AccelerometerSensor {
        return AccelerometerSensor(app)
    }

    @Provides
    @Singleton
    fun provideMagnetometerSensor(app: Application): MagnetometerSensor {
        return MagnetometerSensor(app)
    }
}