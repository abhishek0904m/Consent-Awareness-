package com.example.csuper.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.csuper.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isMonitoringActive: Boolean = true,
    val monitoringSince: Long = System.currentTimeMillis() - 60_000L,
    val activePermissions: List<String> = listOf("Microphone", "Location", "Camera"),
    val sensorEventsCount: Int = 0,
    val uiEventsCount: Int = 0,
    val correlationCount: Int = 0,
    val recentCorrelations: List<String> = emptyList()
)

class DashboardViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState

    init {
        // Update counts from DB
        viewModelScope.launch {
            repository.recentForegroundEvents().collectLatest { events ->
                _uiState.value = _uiState.value.copy(
                    sensorEventsCount = events.size
                    // Map other counts/lists as needed
                )
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.clearAllForegroundEvents()
            _uiState.value = _uiState.value.copy(
                sensorEventsCount = 0,
                uiEventsCount = 0,
                correlationCount = 0,
                recentCorrelations = emptyList()
            )
        }
    }
}