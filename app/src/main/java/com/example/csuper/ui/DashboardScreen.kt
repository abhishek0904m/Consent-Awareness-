package com.example.csuper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.csuper.data.db.ForegroundEvent
import com.example.csuper.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dashboard screen wired to ViewModel:
 * - Monitoring status card
 * - Active permissions card
 * - Statistics card
 * - Recent correlations list
 * - Foreground events from DB
 * - Export Data section
 * - Delete All Data button with snackbar
 * - Floating Action Button
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show snackbar when delete completes
    LaunchedEffect(uiState.showDeleteSnackbar) {
        if (uiState.showDeleteSnackbar) {
            snackbarHostState.showSnackbar("All data deleted")
            viewModel.dismissDeleteSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.refreshStats() }) {
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

            // Monitoring card
            item {
                MonitoringStatusCard(
                    active = uiState.isProfilingActive,
                    sinceTimestamp = uiState.lastRefreshTime.takeIf { it > 0 } ?: System.currentTimeMillis()
                )
            }

            // Active permissions card
            item {
                ActivePermissionsCard(permissions = uiState.activePermissions)
            }

            // Statistics card
            item {
                StatisticsCard(
                    sensorEvents = uiState.correlationStats.totalSensorEvents,
                    uiEvents = uiState.correlationStats.totalUiEvents,
                    correlations = uiState.correlationStats.totalCorrelations
                )
            }

            // Recent correlations
            if (uiState.recentCorrelations.isNotEmpty()) {
                items(uiState.recentCorrelations) { correlation ->
                    CorrelationRow("Correlation #${correlation.id}: ${correlation.sensorEventCount} sensors")
                }
            } else {
                item { EmptyCorrelationsPlaceholder() }
            }

            // Foreground Events from DB
            if (uiState.foregroundEvents.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Foreground Events",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(uiState.foregroundEvents) { event ->
                    ForegroundEventRow(event)
                }
            }

            // Export section
            item {
                DashboardExportSection(
                    sensorEvents = uiState.correlationStats.totalSensorEvents,
                    uiEvents = uiState.correlationStats.totalUiEvents,
                    correlations = uiState.correlationStats.totalCorrelations,
                    appVersionProvider = { "1.0" }
                )
            }

            // Delete all data
            item {
                DeleteAllDataButton {
                    viewModel.deleteAllData()
                }
            }
        }
    }
}

@Composable
fun ForegroundEventRow(event: ForegroundEvent) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val time = dateFormat.format(Date(event.startTime))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = event.packageName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Started: $time",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}