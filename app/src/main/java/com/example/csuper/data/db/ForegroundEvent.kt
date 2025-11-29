package com.example.csuper.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foreground_event")
data class ForegroundEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val startTime: Long,
    val endTime: Long? = null
)