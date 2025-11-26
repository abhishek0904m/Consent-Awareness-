package com.example.csuper.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Privacy Statement:
 * All entities store data locally with encryption. No data is transmitted externally.
 */

/**
 * Represents a sensor event captured from device sensors
 * (accelerometer, gyroscope, light, location, etc.)
 */
@Entity(tableName = "sensor_events")
data class SensorEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val sensorType: String, // e.g., "ACCELEROMETER", "GYROSCOPE", "LOCATION"
    val values: String, // JSON string of sensor values
    val accuracy: Int = 0
)

/**
 * Represents a UI interaction event captured via AccessibilityService
 */
@Entity(tableName = "ui_events")
data class UiEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val packageName: String,
    val eventType: String, // e.g., "CLICK", "FOCUS", "WINDOW_CHANGE"
    val className: String? = null,
    val contentDescription: String? = null,
    val viewIdResourceName: String? = null,
    val text: String? = null
)

/**
 * Represents the correlation result between UI events and nearby sensor events
 */
@Entity(tableName = "correlation_results")
data class CorrelationResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val uiEventId: Long,
    val sensorEventCount: Int, // Number of sensor events within time window
    val correlationWindowMs: Long = 500, // Time window for correlation (±500ms)
    val sensorDataSummary: String, // JSON string with RMS/variance stats
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents user consent for a specific permission
 */
@Entity(tableName = "consent_receipts")
data class ConsentReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val permissionName: String,
    val purpose: String,
    val granted: Boolean,
    val timestamp: Long,
    val revokedAt: Long? = null
)
