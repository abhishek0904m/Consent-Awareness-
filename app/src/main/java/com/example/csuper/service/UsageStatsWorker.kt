package com.example.csuper.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.csuper.data.db.ForegroundEvent
import com.example.csuper.data.db.ForegroundEventDao
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
    private val foregroundEventDao: ForegroundEventDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Only proceed if usage access is granted
        if (!UsageAccess.hasUsageAccess(applicationContext)) {
            return Result.success()
        }

        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Query events from the last 15 minutes
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (15 * 60 * 1000L)

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            // Only store ACTIVITY_RESUMED (i.e., app moved to foreground) events
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                val foregroundEvent = ForegroundEvent(
                    packageName = event.packageName,
                    startTime = event.timeStamp,
                    endTime = null
                )
                foregroundEventDao.insert(foregroundEvent)
            }
        }

        return Result.success()
    }
}
