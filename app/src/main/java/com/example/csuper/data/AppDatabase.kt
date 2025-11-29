package com.example.csuper.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.csuper.data.SensorEventEntity
import com.example.csuper.data.UiEventEntity
import com.example.csuper.data.CorrelationResultEntity
import com.example.csuper.data.db.PermissionUsage
import com.example.csuper.data.db.ForegroundEvent
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.db.PermissionUsageDao
import com.example.csuper.data.db.ForegroundEventDao

@Database(
    entities = [
        SensorEventEntity::class,
        UiEventEntity::class,
        CorrelationResultEntity::class,
        PermissionUsage::class,
        ForegroundEvent::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensorEventDao(): SensorEventDao
    abstract fun uiEventDao(): UiEventDao
    abstract fun correlationResultDao(): CorrelationResultDao
    abstract fun permissionUsageDao(): PermissionUsageDao
    abstract fun foregroundEventDao(): ForegroundEventDao
}