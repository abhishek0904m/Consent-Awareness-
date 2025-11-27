package com.example.csuper.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PermissionUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: PermissionUsage): Long

    @Query("SELECT * FROM permission_usage")
    fun all(): Flow<List<PermissionUsage>>

    @Query("DELETE FROM permission_usage")
    suspend fun deleteAll()
}
