package com.example.csuper.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "consent_preferences")

class ConsentStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val CONSENT_LOCATION = booleanPreferencesKey("consent_location")
        val CONSENT_MICROPHONE = booleanPreferencesKey("consent_microphone")
        val CONSENT_CAMERA = booleanPreferencesKey("consent_camera")
        val CONSENT_SENSORS = booleanPreferencesKey("consent_sensors")
        val CONSENT_ACCESSIBILITY = booleanPreferencesKey("consent_accessibility")
        val CONSENT_USAGE_STATS = booleanPreferencesKey("consent_usage_stats")

        val CONSENT_LOCATION_TIME = longPreferencesKey("consent_location_time")
        val CONSENT_MICROPHONE_TIME = longPreferencesKey("consent_microphone_time")
        val CONSENT_CAMERA_TIME = longPreferencesKey("consent_camera_time")
        val CONSENT_SENSORS_TIME = longPreferencesKey("consent_sensors_time")
        val CONSENT_ACCESSIBILITY_TIME = longPreferencesKey("consent_accessibility_time")
        val CONSENT_USAGE_STATS_TIME = longPreferencesKey("consent_usage_stats_time")

        val IS_PROFILING_ACTIVE = booleanPreferencesKey("is_profiling_active")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    suspend fun setConsent(permissionKey: Preferences.Key<Boolean>, granted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[permissionKey] = granted
            val timeKey = getTimestampKey(permissionKey)
            if (timeKey != null) {
                preferences[timeKey] = System.currentTimeMillis()
            }
        }
    }

    fun getConsent(permissionKey: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[permissionKey] ?: false
        }
    }

    fun getConsentTimestamp(permissionKey: Preferences.Key<Boolean>): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            val timeKey = getTimestampKey(permissionKey)
            timeKey?.let { preferences[it] }
        }
    }

    fun getAllConsents(): Flow<Map<String, Boolean>> {
        return context.dataStore.data.map { preferences ->
            mapOf(
                "location"      to (preferences[CONSENT_LOCATION] ?: false),
                "microphone"    to (preferences[CONSENT_MICROPHONE] ?: false),
                "camera"        to (preferences[CONSENT_CAMERA] ?: false),
                "sensors"       to (preferences[CONSENT_SENSORS] ?: false),
                "accessibility" to (preferences[CONSENT_ACCESSIBILITY] ?: false),
                "usageStats"    to (preferences[CONSENT_USAGE_STATS] ?: false)
            )
        }
    }

    suspend fun setProfilingActive(active: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PROFILING_ACTIVE] = active
        }
    }

    fun isProfilingActive(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_PROFILING_ACTIVE] ?: false
        }
    }

    fun isFirstLaunch(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun getTimestampKey(permissionKey: Preferences.Key<Boolean>): Preferences.Key<Long>? {
        return when (permissionKey) {
            CONSENT_LOCATION      -> CONSENT_LOCATION_TIME
            CONSENT_MICROPHONE    -> CONSENT_MICROPHONE_TIME
            CONSENT_CAMERA        -> CONSENT_CAMERA_TIME
            CONSENT_SENSORS       -> CONSENT_SENSORS_TIME
            CONSENT_ACCESSIBILITY -> CONSENT_ACCESSIBILITY_TIME
            CONSENT_USAGE_STATS   -> CONSENT_USAGE_STATS_TIME
            else -> null
        }
    }
}