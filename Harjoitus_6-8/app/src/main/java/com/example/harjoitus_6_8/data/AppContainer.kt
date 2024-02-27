package com.example.harjoitus_6_8.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val recordRepository: RecordRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val recordRepository: RecordRepository by lazy {
        OfflineRecordRepository(RecordDatabase.getDatabase(context).recordDao())
    }
}