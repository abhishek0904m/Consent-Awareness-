package com.example.csuper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForegroundEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: ForegroundEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<ForegroundEvent>)

    @Query("SELECT * FROM foreground_events ORDER BY startTime DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<ForegroundEvent>>

    @Query("DELETE FROM foreground_events")
    suspend fun deleteAll()
}
