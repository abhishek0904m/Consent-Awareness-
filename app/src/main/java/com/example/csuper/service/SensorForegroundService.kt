package com.example.csuper.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.csuper.MainActivity
import com.example.csuper.R
import com.example.csuper.data.SensorEventEntity
import com.example.csuper.data.dao.SensorEventDao
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground Service for collecting sensor data
 * Monitors accelerometer, gyroscope, light sensor, and location
 * Runs with visible notification to ensure user transparency
 * 
 * Privacy Statement:
 * This service collects sensor data only with user consent.
 * All data is stored locally and encrypted. No external transmission occurs.
 */
@AndroidEntryPoint
class SensorForegroundService : Service(), SensorEventListener, LocationListener {
    
    @Inject
    lateinit var sensorEventDao: SensorEventDao
    
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private val gson = Gson()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var lightSensor: Sensor? = null
    
    private var sensorUpdateCount = 0L
    private var lastStatsUpdateTime = System.currentTimeMillis()
    
    companion object {
        private const val CHANNEL_ID = "CsuperSensorChannel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.example.csuper.START_SENSOR_SERVICE"
        const val ACTION_STOP = "com.example.csuper.STOP_SENSOR_SERVICE"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        // Get available sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForegroundService()
                startSensorCollection()
            }
            ACTION_STOP -> {
                stopSensorCollection()
                stopSelf()
            }
        }
        return START_STICKY
    }
    
    private fun startForegroundService() {
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }
    
    private fun startSensorCollection() {
        // Register sensor listeners
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        // Request location updates (requires permission check in production)
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // 5 seconds
                10f, // 10 meters
                this
            )
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    private fun stopSensorCollection() {
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        val sensorType = when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> "ACCELEROMETER"
            Sensor.TYPE_GYROSCOPE -> "GYROSCOPE"
            Sensor.TYPE_LIGHT -> "LIGHT"
            else -> "UNKNOWN"
        }
        
        val sensorEvent = SensorEventEntity(
            timestamp = System.currentTimeMillis(),
            sensorType = sensorType,
            values = gson.toJson(event.values),
            accuracy = event.accuracy
        )
        
        serviceScope.launch {
            sensorEventDao.insert(sensorEvent)
        }
        
        sensorUpdateCount++
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used for this implementation
    }
    
    override fun onLocationChanged(location: Location) {
        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "altitude" to location.altitude,
            "accuracy" to location.accuracy,
            "speed" to location.speed
        )
        
        val sensorEvent = SensorEventEntity(
            timestamp = System.currentTimeMillis(),
            sensorType = "LOCATION",
            values = gson.toJson(locationData),
            accuracy = location.accuracy.toInt()
        )
        
        serviceScope.launch {
            sensorEventDao.insert(sensorEvent)
        }
        
        sensorUpdateCount++
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopSensorCollection()
        serviceScope.cancel()
    }
}
