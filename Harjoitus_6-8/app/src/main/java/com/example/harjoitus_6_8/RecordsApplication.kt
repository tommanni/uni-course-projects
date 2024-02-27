package com.example.harjoitus_6_8

import android.app.Application
import com.example.harjoitus_6_8.data.AppContainer
import com.example.harjoitus_6_8.data.AppDataContainer

class RecordsApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}