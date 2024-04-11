package com.example.disctrack.data.repository

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.disctrack.workers.CourseDataSyncWorker
import java.util.concurrent.TimeUnit


class WorkManagerCourseDataSyncRepository(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    // Synchronize course data every 7 days
    fun syncCourseData() {
        // Make sure that device is connected to internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


        val syncRequest = PeriodicWorkRequestBuilder<CourseDataSyncWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                10,
                TimeUnit.MINUTES
            ).build()

        // Ensure that only one instance of synchronization task is scheduled at a time
        workManager.enqueueUniquePeriodicWork(
            "courseDataSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}