package com.example.csuper

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.csuper.service.WorkScheduler
import com.example.csuper.service.UsageStatsWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CsuperApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // Schedule periodic work for usage stats collection
        WorkScheduler.schedule(this)

        // TEMP: trigger one-time collection now (remove after verifying)
        val oneTime = OneTimeWorkRequestBuilder<UsageStatsWorker>().build()
        WorkManager.getInstance(this)
            .enqueueUniqueWork("usage_stats_one_time", ExistingWorkPolicy.REPLACE, oneTime)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}