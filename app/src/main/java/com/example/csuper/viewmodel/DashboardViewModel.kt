package com.example.csuper.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.csuper.correlation.CorrelationEngine
import com.example.csuper.correlation.CorrelationStats
import com.example.csuper.data.CorrelationResultEntity
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import com.example.csuper.service.SensorForegroundService
import com.example.csuper.util.ConsentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Dashboard Screen
 * Manages profiling state, statistics, and data operations
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val consentStore: ConsentStore,
    private val correlationEngine: CorrelationEngine,
    private val sensorEventDao: SensorEventDao,
    private val uiEventDao: UiEventDao,
    private val correlationResultDao: CorrelationResultDao
) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
        observeProfilingState()
        observeRecentCorrelations()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            consentStore.getAllConsents().collect { consents ->
                _uiState.value = _uiState.value.copy(
                    activePermissions = consents.filter { it.value }.keys.toList()
                )
            }
        }
        
        viewModelScope.launch {
            val stats = correlationEngine.getCorrelationStats()
            _uiState.value = _uiState.value.copy(
                correlationStats = stats
            )
        }
    }
    
    private fun observeProfilingState() {
        viewModelScope.launch {
            consentStore.isProfilingActive().collect { isActive ->
                _uiState.value = _uiState.value.copy(isProfilingActive = isActive)
            }
        }
    }
    
    private fun observeRecentCorrelations() {
        viewModelScope.launch {
            correlationResultDao.getRecentResults(20).collect { results ->
                _uiState.value = _uiState.value.copy(recentCorrelations = results)
            }
        }
    }
    
    fun startProfiling() {
        viewModelScope.launch {
            consentStore.setProfilingActive(true)
            
            // Start sensor foreground service
            val intent = Intent(getApplication(), SensorForegroundService::class.java).apply {
                action = SensorForegroundService.ACTION_START
            }
            getApplication<Application>().startForegroundService(intent)
            
            _uiState.value = _uiState.value.copy(isProfilingActive = true)
        }
    }
    
    fun stopProfiling() {
        viewModelScope.launch {
            consentStore.setProfilingActive(false)
            
            // Stop sensor foreground service
            val intent = Intent(getApplication(), SensorForegroundService::class.java).apply {
                action = SensorForegroundService.ACTION_STOP
            }
            getApplication<Application>().startService(intent)
            
            _uiState.value = _uiState.value.copy(isProfilingActive = false)
        }
    }
    
    fun refreshStats() {
        viewModelScope.launch {
            val stats = correlationEngine.getCorrelationStats()
            _uiState.value = _uiState.value.copy(
                correlationStats = stats,
                lastRefreshTime = System.currentTimeMillis()
            )
        }
    }
    
    fun deleteAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            
            sensorEventDao.deleteAll()
            uiEventDao.deleteAll()
            correlationResultDao.deleteAll()
            
            val stats = correlationEngine.getCorrelationStats()
            _uiState.value = _uiState.value.copy(
                correlationStats = stats,
                isDeleting = false,
                recentCorrelations = emptyList()
            )
        }
    }
    
    fun exportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            
            // In a production app, this would export data to a file
            // For now, we just simulate the operation
            kotlinx.coroutines.delay(1000)
            
            _uiState.value = _uiState.value.copy(
                isExporting = false,
                exportSuccess = true
            )
        }
    }
    
    fun dismissExportSuccess() {
        _uiState.value = _uiState.value.copy(exportSuccess = false)
    }
}

data class DashboardUiState(
    val isProfilingActive: Boolean = false,
    val activePermissions: List<String> = emptyList(),
    val correlationStats: CorrelationStats = CorrelationStats(0, 0, 0),
    val recentCorrelations: List<CorrelationResultEntity> = emptyList(),
    val lastRefreshTime: Long = 0,
    val isDeleting: Boolean = false,
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false
)
