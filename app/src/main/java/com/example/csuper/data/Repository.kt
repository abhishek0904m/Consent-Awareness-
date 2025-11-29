package com.example.csuper.data

import com.example.csuper.data.db.ForegroundEvent
import com.example.csuper.data.db.ForegroundEventDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val foregroundEventDao: ForegroundEventDao
) {
    suspend fun insertForegroundEvents(events: List<ForegroundEvent>) {
        if (events.isNotEmpty()) {
            foregroundEventDao.insertAll(events)
        }
    }

    fun recentForegroundEvents(limit: Int = 100): Flow<List<ForegroundEvent>> {
        return foregroundEventDao.recent(limit)
    }

    suspend fun clearAllForegroundEvents() {
        foregroundEventDao.clearAll()
    }
}