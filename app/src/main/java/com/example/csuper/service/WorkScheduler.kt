package com.example.csuper.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules periodic work for UsageStatsWorker.
 */
object WorkScheduler {

    private const val WORK_NAME = "usage_stats_worker"

    /**
     * Schedules the UsageStatsWorker to run periodically every 15 minutes.
     * Should be called from CsuperApplication.onCreate().
     * @param context The application context
     */
    fun schedule(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<UsageStatsWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Cancels the scheduled periodic work.
     * @param context The application context
     */
    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
