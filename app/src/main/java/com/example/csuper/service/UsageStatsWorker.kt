package com.example.csuper.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.csuper.data.Repository
import com.example.csuper.data.db.ForegroundEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UsageStatsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("UsageStatsWorker", "Starting doWork")
        if (!hasUsageAccess(applicationContext)) {
            Log.w("UsageStatsWorker", "hasUsageAccess=false; skipping")
            return@withContext Result.success()
        }

        val now = System.currentTimeMillis()
        val start = now - 60 * 60 * 1000L
        val usm = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageEvents = usm.queryEvents(start, now)

        val parsed = mutableListOf<ForegroundEvent>()
        val e = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(e)
            if (e.eventType == UsageEvents.Event.ACTIVITY_RESUMED && !e.packageName.isNullOrBlank()) {
                parsed.add(ForegroundEvent(packageName = e.packageName, startTime = e.timeStamp))
            }
        }

        Log.d("UsageStatsWorker", "parsedEvents=${parsed.size}")
        return@withContext try {
            repository.insertForegroundEvents(parsed)
            Log.d("UsageStatsWorker", "inserted=${parsed.size}")
            Result.success()
        } catch (t: Throwable) {
            Log.e("UsageStatsWorker", "insert failed", t)
            Result.failure()
        }
    }

    companion object {
        fun hasUsageAccess(context: Context): Boolean {
            return try {
                Settings.Secure.getInt(context.contentResolver, "usage_access_enabled", 0) == 1
            } catch (ex: Exception) {
                false
            }
        }
    }
}