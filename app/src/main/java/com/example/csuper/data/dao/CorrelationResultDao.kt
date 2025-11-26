package com.example.csuper.data.dao

import androidx.room.*
import com.example.csuper.data.CorrelationResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for correlation results
 */
@Dao
interface CorrelationResultDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: CorrelationResultEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<CorrelationResultEntity>)
    
    @Query("SELECT * FROM correlation_results ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 50): Flow<List<CorrelationResultEntity>>
    
    @Query("SELECT * FROM correlation_results WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp")
    suspend fun getResultsByTimeRange(startTime: Long, endTime: Long): List<CorrelationResultEntity>
    
    @Query("SELECT * FROM correlation_results WHERE uiEventId = :uiEventId")
    suspend fun getResultsByUiEvent(uiEventId: Long): List<CorrelationResultEntity>
    
    @Query("SELECT COUNT(*) FROM correlation_results")
    suspend fun getCount(): Int
    
    @Query("DELETE FROM correlation_results")
    suspend fun deleteAll()
    
    @Query("DELETE FROM correlation_results WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}
