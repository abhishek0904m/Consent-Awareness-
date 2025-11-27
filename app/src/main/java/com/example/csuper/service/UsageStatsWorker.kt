package com.example.csuper.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.csuper.data.Repository
import com.example.csuper.data.db.ForegroundEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager Worker that queries UsageStatsManager for recent UsageEvents
 * and stores ACTIVITY_RESUMED events as ForegroundEvent entries.
 */
@HiltWorker
class UsageStatsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "UsageStatsWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: Starting usage stats collection")

        // Only proceed if usage access is granted
        if (!UsageAccess.hasUsageAccess(applicationContext)) {
            Log.d(TAG, "doWork: Usage access not granted, returning success")
            return Result.success()
        }
        Log.d(TAG, "doWork: Usage access granted, proceeding")

        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Query events from the last hour
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (60 * 60 * 1000L)

        Log.d(TAG, "doWork: Querying events from $startTime to $endTime")

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()
        val foregroundEvents = mutableListOf<ForegroundEvent>()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            // Only store ACTIVITY_RESUMED (i.e., app moved to foreground) events
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                foregroundEvents.add(
                    ForegroundEvent(
                        packageName = event.packageName,
                        startTime = event.timeStamp,
                        endTime = null
                    )
                )
            }
        }

        Log.d(TAG, "doWork: Parsed ${foregroundEvents.size} ACTIVITY_RESUMED events")

        if (foregroundEvents.isNotEmpty()) {
            repository.insertForegroundEvents(foregroundEvents)
            Log.d(TAG, "doWork: Inserted ${foregroundEvents.size} foreground events")
        }

        Log.d(TAG, "doWork: Completed successfully")
        return Result.success()
    }
}
