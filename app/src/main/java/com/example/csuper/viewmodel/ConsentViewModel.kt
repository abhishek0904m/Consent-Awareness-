package com.example.csuper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.csuper.util.ConsentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Consent Screen
 * Manages permission consent state and user preferences
 */
@HiltViewModel
class ConsentViewModel @Inject constructor(
    private val consentStore: ConsentStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState.asStateFlow()

    init {
        loadConsentState()
    }

    private fun loadConsentState() {
        viewModelScope.launch {
            consentStore.getAllConsents().collect { consents ->
                _uiState.value = _uiState.value.copy(
                    locationConsent = consents["location"] ?: false,
                    microphoneConsent = consents["microphone"] ?: false,
                    cameraConsent = consents["camera"] ?: false,
                    sensorsConsent = consents["sensors"] ?: false,
                    accessibilityConsent = consents["accessibility"] ?: false,
                    usageStatsConsent = consents["usageStats"] ?: false
                )
            }
        }
    }

    fun toggleLocationConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.locationConsent
            consentStore.setConsent(ConsentStore.CONSENT_LOCATION, newValue)
            _uiState.value = _uiState.value.copy(locationConsent = newValue)
        }
    }

    fun toggleMicrophoneConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.microphoneConsent
            consentStore.setConsent(ConsentStore.CONSENT_MICROPHONE, newValue)
            _uiState.value = _uiState.value.copy(microphoneConsent = newValue)
        }
    }

    fun toggleCameraConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.cameraConsent
            consentStore.setConsent(ConsentStore.CONSENT_CAMERA, newValue)
            _uiState.value = _uiState.value.copy(cameraConsent = newValue)
        }
    }

    fun toggleSensorsConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.sensorsConsent
            consentStore.setConsent(ConsentStore.CONSENT_SENSORS, newValue)
            _uiState.value = _uiState.value.copy(sensorsConsent = newValue)
        }
    }

    fun toggleAccessibilityConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.accessibilityConsent
            consentStore.setConsent(ConsentStore.CONSENT_ACCESSIBILITY, newValue)
            _uiState.value = _uiState.value.copy(accessibilityConsent = newValue)
        }
    }

    fun toggleUsageStatsConsent() {
        viewModelScope.launch {
            val newValue = !_uiState.value.usageStatsConsent
            consentStore.setConsent(ConsentStore.CONSENT_USAGE_STATS, newValue)
            _uiState.value = _uiState.value.copy(usageStatsConsent = newValue)
        }
    }

    fun completeConsent() {
        viewModelScope.launch {
            consentStore.setFirstLaunchCompleted()
        }
    }
}

data class ConsentUiState(
    val locationConsent: Boolean = false,
    val microphoneConsent: Boolean = false,
    val cameraConsent: Boolean = false,
    val sensorsConsent: Boolean = false,
    val accessibilityConsent: Boolean = false,
    val usageStatsConsent: Boolean = false
)