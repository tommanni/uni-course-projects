package com.example.disctrack.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.disctrack.data.database.entities.toCourseItem
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


private const val TAG = "CourseDataSyncWorker"

/**
 * Worker to synchronize local data with api data
 */
@HiltWorker
class CourseDataSyncWorker @AssistedInject constructor(
    private val courseDbRepository: CourseDbRepository,
    private val courseRepository: CourseRepository,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(ctx, params) {

    // Define what work worker does
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                // Clear old data from DB
                courseDbRepository.deleteAllCourses()
                val coursesResponse = courseRepository.getAllCourses()
                // Filter out no longer existing courses and non-parent courses and courses
                // that have empty fields
                val filteredCourses = coursesResponse.courses?.filter { item ->
                            item.endDate == null
                            && item.parentId == null
                            && item.lon != ""
                            && item.lat != ""
                            && item.city != ""
                            && item.area != ""
                }

                filteredCourses?.forEach { course ->
                    courseDbRepository.insert(course.toCourseItem())
                }

                Result.success()
            } catch(throwable: Throwable) {
                Log.e(TAG, "Error synchronizing local data with api data")
                Result.failure()
            }
        }

    }
}