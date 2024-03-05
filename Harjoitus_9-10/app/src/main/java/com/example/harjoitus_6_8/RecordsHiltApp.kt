package com.example.harjoitus_6_8

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RecordsHiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}