package com.example.disctrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * A base class for the application that serves as the application-level dependency container
 */
@HiltAndroidApp
class DiscTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}