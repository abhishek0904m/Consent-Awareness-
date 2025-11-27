package com.example.csuper.ui

import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonitoringStatusCard(active: Boolean, sinceTimestamp: Long) {
    val date = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(sinceTimestamp))
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = if (active) "Monitoring Active" else "Monitoring Paused",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Since: $date", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ActivePermissionsCard(permissions: List<String>) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text("Active Permissions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            permissions.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
fun StatisticsCard(sensorEvents: Int, uiEvents: Int, correlations: Int) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text("Statistics", style = MaterialTheme.typography.titleMedium)
            Text("Sensor Events: $sensorEvents", style = MaterialTheme.typography.bodySmall)
            Text("UI Events: $uiEvents", style = MaterialTheme.typography.bodySmall)
            Text("Correlations: $correlations", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CorrelationRow(label: String) {
    Card(Modifier.padding(vertical = 4.dp)) {
        Text(label, Modifier.padding(12.dp))
    }
}

@Composable
fun EmptyCorrelationsPlaceholder() {
    Card {
        Text("No recent correlations", Modifier.padding(16.dp))
    }
}

@Composable
fun DeleteAllDataButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Delete All Data")
    }
}