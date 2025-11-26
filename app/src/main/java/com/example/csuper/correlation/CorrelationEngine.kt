package com.example.csuper.correlation

import com.example.csuper.data.CorrelationResultEntity
import com.example.csuper.data.SensorEventEntity
import com.example.csuper.data.UiEventEntity
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Correlation Engine that finds relationships between UI events and sensor data
 * Uses a time window approach to find nearby sensor events for each UI interaction
 * 
 * Privacy Statement:
 * Correlation analysis happens locally. Results are stored encrypted and never transmitted.
 */
@Singleton
class CorrelationEngine @Inject constructor(
    private val sensorEventDao: SensorEventDao,
    private val uiEventDao: UiEventDao,
    private val correlationResultDao: CorrelationResultDao
) {
    
    private val gson = Gson()
    private val correlationWindowMs = 500L // ±500ms window
    
    /**
     * Correlate a UI event with nearby sensor events
     * Finds sensor events within ±500ms of the UI event timestamp
     */
    suspend fun correlateUiEvent(uiEvent: UiEventEntity) = withContext(Dispatchers.IO) {
        val startTime = uiEvent.timestamp - correlationWindowMs
        val endTime = uiEvent.timestamp + correlationWindowMs
        
        // Get sensor events within the time window
        val sensorEvents = sensorEventDao.getEventsByTimeRange(startTime, endTime)
        
        if (sensorEvents.isNotEmpty()) {
            // Compute statistics
            val summary = computeSensorDataSummary(sensorEvents)
            
            // Create correlation result
            val correlationResult = CorrelationResultEntity(
                timestamp = uiEvent.timestamp,
                uiEventId = uiEvent.id,
                sensorEventCount = sensorEvents.size,
                correlationWindowMs = correlationWindowMs,
                sensorDataSummary = gson.toJson(summary)
            )
            
            correlationResultDao.insert(correlationResult)
        }
    }
    
    /**
     * Batch correlate recent UI events
     * Useful for periodic processing
     */
    suspend fun correlateRecentEvents(limit: Int = 100) = withContext(Dispatchers.IO) {
        // Get recent UI events that haven't been correlated yet
        // In a production system, you'd track which events have been processed
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (60 * 1000) // Last minute
        
        val uiEvents = uiEventDao.getEventsByTimeRange(startTime, endTime)
        
        uiEvents.forEach { uiEvent ->
            correlateUiEvent(uiEvent)
        }
    }
    
    /**
     * Compute statistical summary of sensor data
     * Returns RMS, mean, variance, and sensor type distribution
     */
    private fun computeSensorDataSummary(sensorEvents: List<SensorEventEntity>): Map<String, Any> {
        val sensorTypeCount = mutableMapOf<String, Int>()
        val allValues = mutableListOf<Float>()
        
        sensorEvents.forEach { event ->
            // Count by sensor type
            sensorTypeCount[event.sensorType] = (sensorTypeCount[event.sensorType] ?: 0) + 1
            
            // Parse sensor values
            try {
                val values = gson.fromJson(event.values, FloatArray::class.java)
                values?.let { allValues.addAll(it.toList()) }
            } catch (e: Exception) {
                // Skip malformed data
            }
        }
        
        // Compute statistics
        val mean = if (allValues.isNotEmpty()) allValues.average() else 0.0
        val variance = if (allValues.isNotEmpty()) {
            allValues.map { (it - mean) * (it - mean) }.average()
        } else 0.0
        val rms = if (allValues.isNotEmpty()) {
            sqrt(allValues.map { it * it }.average())
        } else 0.0
        
        return mapOf(
            "sensorTypeCount" to sensorTypeCount,
            "totalSamples" to allValues.size,
            "mean" to mean,
            "variance" to variance,
            "rms" to rms,
            "minValue" to (allValues.minOrNull() ?: 0f),
            "maxValue" to (allValues.maxOrNull() ?: 0f)
        )
    }
    
    /**
     * Get correlation statistics for dashboard
     */
    suspend fun getCorrelationStats(): CorrelationStats = withContext(Dispatchers.IO) {
        val sensorCount = sensorEventDao.getCount()
        val uiCount = uiEventDao.getCount()
        val correlationCount = correlationResultDao.getCount()
        
        CorrelationStats(
            totalSensorEvents = sensorCount,
            totalUiEvents = uiCount,
            totalCorrelations = correlationCount
        )
    }
}

/**
 * Data class for correlation statistics
 */
data class CorrelationStats(
    val totalSensorEvents: Int,
    val totalUiEvents: Int,
    val totalCorrelations: Int
)
