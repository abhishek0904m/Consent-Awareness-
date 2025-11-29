package com.example.csuper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForegroundEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<ForegroundEvent>)

    @Query("SELECT * FROM foreground_event ORDER BY startTime DESC LIMIT :limit")
    fun recent(limit: Int = 100): Flow<List<ForegroundEvent>>

    @Query("DELETE FROM foreground_event")
    suspend fun clearAll()
}