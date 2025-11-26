package com.example.csuper.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import com.example.csuper.data.UiEventEntity
import com.example.csuper.data.dao.UiEventDao
import com.example.csuper.correlation.CorrelationEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Accessibility Service for tracking UI interactions
 * Captures app usage, clicks, focus changes, and window state changes
 * 
 * Privacy Statement:
 * This service monitors UI interactions only with explicit user consent.
 * All data is stored locally and encrypted. No personal data is extracted from text fields.
 * No data is transmitted to external servers.
 */
@AndroidEntryPoint
class UiAccessibilityService : AccessibilityService() {
    
    @Inject
    lateinit var uiEventDao: UiEventDao
    
    @Inject
    lateinit var correlationEngine: CorrelationEngine
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        
        // Configure accessibility service
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_VIEW_CLICKED or
                    AccessibilityEvent.TYPE_VIEW_FOCUSED
            
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        
        serviceInfo = info
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Filter out system events to protect privacy
        if (event.packageName == null || event.packageName.toString().isEmpty()) {
            return
        }
        
        val eventType = when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "WINDOW_CHANGE"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> "CONTENT_CHANGE"
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "CLICK"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "FOCUS"
            else -> "OTHER"
        }
        
        // Extract non-sensitive information only
        val uiEvent = UiEventEntity(
            timestamp = System.currentTimeMillis(),
            packageName = event.packageName?.toString() ?: "unknown",
            eventType = eventType,
            className = event.className?.toString(),
            contentDescription = event.contentDescription?.toString()?.take(50), // Limit length for privacy
            viewIdResourceName = try {
                event.source?.viewIdResourceName?.take(100)
            } catch (e: Exception) {
                null
            },
            text = null // We intentionally don't capture text content for privacy
        )
        
        serviceScope.launch {
            val eventId = uiEventDao.insert(uiEvent)
            
            // Trigger correlation with sensor events
            val insertedEvent = uiEvent.copy(id = eventId)
            correlationEngine.correlateUiEvent(insertedEvent)
        }
    }
    
    override fun onInterrupt() {
        // Service was interrupted
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
