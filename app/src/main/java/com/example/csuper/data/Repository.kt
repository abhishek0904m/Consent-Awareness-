package com.example.csuper.data

import com.example.csuper.data.db.ForegroundEvent
import com.example.csuper.data.db.ForegroundEventDao
import com.example.csuper.data.db.PermissionUsage
import com.example.csuper.data.db.PermissionUsageDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing foreground events and permission usage data.
 * Wraps the DAOs and provides Flow-based access patterns.
 */
@Singleton
class Repository @Inject constructor(
    private val foregroundEventDao: ForegroundEventDao,
    private val permissionUsageDao: PermissionUsageDao
) {

    /**
     * Returns a Flow of the most recent foreground events.
     * @param limit The maximum number of events to return (default 50)
     */
    fun recentForegroundEvents(limit: Int = 50): Flow<List<ForegroundEvent>> {
        return foregroundEventDao.recent(limit)
    }

    /**
     * Returns a Flow of all permission usage records.
     */
    fun permissionUsage(): Flow<List<PermissionUsage>> {
        return permissionUsageDao.all()
    }

    /**
     * Inserts a new foreground event.
     */
    suspend fun insertForegroundEvent(event: ForegroundEvent): Long {
        return foregroundEventDao.insert(event)
    }

    /**
     * Inserts multiple foreground events in a single transaction.
     * @param events The list of events to insert
     */
    suspend fun insertForegroundEvents(events: List<ForegroundEvent>) {
        foregroundEventDao.insertAll(events)
    }

    /**
     * Inserts a new permission usage record.
     */
    suspend fun insertPermissionUsage(usage: PermissionUsage): Long {
        return permissionUsageDao.insert(usage)
    }

    /**
     * Clears all foreground events from the database.
     */
    suspend fun clearForegroundEvents() {
        foregroundEventDao.deleteAll()
    }

    /**
     * Clears all permission usage records from the database.
     */
    suspend fun clearPermissionUsage() {
        permissionUsageDao.deleteAll()
    }

    /**
     * Clears all data (foreground events and permission usage).
     */
    suspend fun clearAll() {
        foregroundEventDao.deleteAll()
        permissionUsageDao.deleteAll()
    }
}
