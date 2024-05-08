package com.example.disctrack

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.disctrack.data.repository.WorkManagerCourseDataSyncRepository
import com.example.disctrack.workers.CustomWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * A base class for the application that serves as the application-level dependency container
 */
@HiltAndroidApp
class DiscTrackApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory : CustomWorkerFactory
    override fun onCreate() {
        super.onCreate()

        // Initialize WorkManager with the custom WorkerFactory
        WorkManager.initialize(this, workManagerConfiguration)

        // Data synchronization
        val syncRepository = WorkManagerCourseDataSyncRepository(this)
        syncRepository.syncCourseData()
    }

    // Configuration object for WorkManager
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


}