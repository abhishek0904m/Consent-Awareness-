package com.example.csuper.data.dao

import androidx.room.*
import com.example.csuper.data.UiEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UI interaction events
 */
@Dao
interface UiEventDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: UiEventEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<UiEventEntity>)
    
    @Query("SELECT * FROM ui_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 100): Flow<List<UiEventEntity>>
    
    @Query("SELECT * FROM ui_events WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp")
    suspend fun getEventsByTimeRange(startTime: Long, endTime: Long): List<UiEventEntity>
    
    @Query("SELECT * FROM ui_events WHERE packageName = :packageName ORDER BY timestamp DESC LIMIT :limit")
    fun getEventsByPackage(packageName: String, limit: Int = 100): Flow<List<UiEventEntity>>
    
    @Query("SELECT COUNT(*) FROM ui_events")
    suspend fun getCount(): Int
    
    @Query("DELETE FROM ui_events")
    suspend fun deleteAll()
    
    @Query("DELETE FROM ui_events WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
