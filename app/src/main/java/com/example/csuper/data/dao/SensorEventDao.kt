package com.example.csuper.data.dao

import androidx.room.*
import com.example.csuper.data.SensorEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for sensor events
 */
@Dao
interface SensorEventDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: SensorEventEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<SensorEventEntity>)
    
    @Query("SELECT * FROM sensor_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 100): Flow<List<SensorEventEntity>>
    
    @Query("SELECT * FROM sensor_events WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp")
    suspend fun getEventsByTimeRange(startTime: Long, endTime: Long): List<SensorEventEntity>
    
    @Query("SELECT * FROM sensor_events WHERE sensorType = :type ORDER BY timestamp DESC LIMIT :limit")
    fun getEventsBySensorType(type: String, limit: Int = 100): Flow<List<SensorEventEntity>>
    
    @Query("SELECT COUNT(*) FROM sensor_events")
    suspend fun getCount(): Int
    
    @Query("DELETE FROM sensor_events")
    suspend fun deleteAll()
    
    @Query("DELETE FROM sensor_events WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
