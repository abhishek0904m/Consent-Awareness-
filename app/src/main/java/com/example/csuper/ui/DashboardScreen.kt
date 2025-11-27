package com.example.csuper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Restored full dashboard structure:
 * - Monitoring status card
 * - Active permissions card
 * - Statistics card
 * - Recent correlations list
 * - Export Data section
 * - Delete All Data button
 * - Floating Action Button
 *
 * Replace the placeholder composable (MonitoringStatusCard, ActivePermissionsCard, StatisticsCard,
 * CorrelationRow, EmptyCorrelationsPlaceholder, DeleteAllDataButton) with your real ones if you have them.
 */
@Composable
fun DashboardScreen(
    // Replace these with real ViewModel/state values later
    isMonitoringActive: Boolean = true,
    monitoringSince: Long = System.currentTimeMillis() - 60_000L,
    activePermissions: List<String> = listOf("Microphone", "Location", "Camera"),
    sensorEventsCount: Int = 10810,
    uiEventsCount: Int = 23,
    correlationCount: Int = 5,
    recentCorrelations: List<String> = listOf("Mic + UI Tap", "Location + App Open"),

    // Callbacks
    onDeleteAllData: () -> Unit = {},
    onFabClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Text("+")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Privacy Dashboard",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Monitoring card (placeholder)
            item {
                MonitoringStatusCard(
                    active = isMonitoringActive,
                    sinceTimestamp = monitoringSince
                )
            }

            // Active permissions card (placeholder)
            item {
                ActivePermissionsCard(permissions = activePermissions)
            }

            // Statistics card (placeholder)
            item {
                StatisticsCard(
                    sensorEvents = sensorEventsCount,
                    uiEvents = uiEventsCount,
                    correlations = correlationCount
                )
            }

            // Recent correlations (placeholder list)
            if (recentCorrelations.isNotEmpty()) {
                items(recentCorrelations) { label ->
                    CorrelationRow(label)
                }
            } else {
                item { EmptyCorrelationsPlaceholder() }
            }

            // Export section (your working button)
            item {
                DashboardExportSection(
                    sensorEvents = sensorEventsCount,
                    uiEvents = uiEventsCount,
                    correlations = correlationCount,
                    appVersionProvider = { "1.0" } // Replace with BuildConfig.VERSION_NAME later
                )
            }

            // Delete all data (placeholder)
            item {
                DeleteAllDataButton {
                    onDeleteAllData()
                    scope.launch {
                        snackbarHostState.showSnackbar("All data deleted")
                    }
                }
            }
        }
    }
}