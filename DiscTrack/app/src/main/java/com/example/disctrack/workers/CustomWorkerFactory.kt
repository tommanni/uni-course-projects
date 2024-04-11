package com.example.disctrack.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom Worker Factory that knows how to create instances of worker classes with required
 * dependencies
 */
@Singleton
class CustomWorkerFactory @Inject constructor(
    private val courseDbRepository: CourseDbRepository,
    private val courseRepository: CourseRepository
): WorkerFactory() {

    // Return the correct worker based on workerClassName
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            CourseDataSyncWorker::class.java.name ->
                CourseDataSyncWorker(
                    courseDbRepository,
                    courseRepository,
                    appContext,
                    workerParameters
                )
            else -> null
        }
    }
}