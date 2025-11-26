package com.example.csuper.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for managing user consent preferences
 * Stores consent status for various permissions
 * 
 * Privacy Statement:
 * Consent data is stored locally and can be revoked at any time.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "consent_preferences")

class ConsentStore(private val context: Context) {
    
    companion object {
        // Permission consent keys
        val CONSENT_LOCATION = booleanPreferencesKey("consent_location")
        val CONSENT_MICROPHONE = booleanPreferencesKey("consent_microphone")
        val CONSENT_CAMERA = booleanPreferencesKey("consent_camera")
        val CONSENT_SENSORS = booleanPreferencesKey("consent_sensors")
        val CONSENT_ACCESSIBILITY = booleanPreferencesKey("consent_accessibility")
        val CONSENT_USAGE_STATS = booleanPreferencesKey("consent_usage_stats")
        
        // Consent timestamps
        val CONSENT_LOCATION_TIME = longPreferencesKey("consent_location_time")
        val CONSENT_MICROPHONE_TIME = longPreferencesKey("consent_microphone_time")
        val CONSENT_CAMERA_TIME = longPreferencesKey("consent_camera_time")
        val CONSENT_SENSORS_TIME = longPreferencesKey("consent_sensors_time")
        val CONSENT_ACCESSIBILITY_TIME = longPreferencesKey("consent_accessibility_time")
        val CONSENT_USAGE_STATS_TIME = longPreferencesKey("consent_usage_stats_time")
        
        // Profiling status
        val IS_PROFILING_ACTIVE = booleanPreferencesKey("is_profiling_active")
        
        // First launch
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }
    
    /**
     * Set consent for a specific permission
     */
    suspend fun setConsent(permissionKey: Preferences.Key<Boolean>, granted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[permissionKey] = granted
            // Store timestamp
            val timeKey = getTimestampKey(permissionKey)
            if (timeKey != null) {
                preferences[timeKey] = System.currentTimeMillis()
            }
        }
    }
    
    /**
     * Get consent flow for a specific permission
     */
    fun getConsent(permissionKey: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[permissionKey] ?: false
        }
    }
    
    /**
     * Get consent timestamp for a specific permission
     */
    fun getConsentTimestamp(permissionKey: Preferences.Key<Boolean>): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            val timeKey = getTimestampKey(permissionKey)
            timeKey?.let { preferences[it] }
        }
    }
    
    /**
     * Get all consents as a map
     */
    fun getAllConsents(): Flow<Map<String, Boolean>> {
        return context.dataStore.data.map { preferences ->
            mapOf(
                "location" to (preferences[CONSENT_LOCATION] ?: false),
                "microphone" to (preferences[CONSENT_MICROPHONE] ?: false),
                "camera" to (preferences[CONSENT_CAMERA] ?: false),
                "sensors" to (preferences[CONSENT_SENSORS] ?: false),
                "accessibility" to (preferences[CONSENT_ACCESSIBILITY] ?: false),
                "usageStats" to (preferences[CONSENT_USAGE_STATS] ?: false)
            )
        }
    }
    
    /**
     * Set profiling active status
     */
    suspend fun setProfilingActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PROFILING_ACTIVE] = active
        }
    }
    
    /**
     * Get profiling active status
     */
    fun isProfilingActive(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_PROFILING_ACTIVE] ?: false
        }
    }
    
    /**
     * Check if this is the first launch
     */
    fun isFirstLaunch(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }
    }
    
    /**
     * Mark first launch as completed
     */
    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }
    
    /**
     * Clear all consent data
     */
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    /**
     * Helper function to get timestamp key for a permission key
     */
    private fun getTimestampKey(permissionKey: Preferences.Key<Boolean>): Preferences.Key<Long>? {
        return when (permissionKey) {
            CONSENT_LOCATION -> CONSENT_LOCATION_TIME
            CONSENT_MICROPHONE -> CONSENT_MICROPHONE_TIME
            CONSENT_CAMERA -> CONSENT_CAMERA_TIME
            CONSENT_SENSORS -> CONSENT_SENSORS_TIME
            CONSENT_ACCESSIBILITY -> CONSENT_ACCESSIBILITY_TIME
            CONSENT_USAGE_STATS -> CONSENT_USAGE_STATS_TIME
            else -> null
        }
    }
}
