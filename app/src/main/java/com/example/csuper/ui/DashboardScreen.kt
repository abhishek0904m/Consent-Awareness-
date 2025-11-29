@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.csuper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen() {
    val monitoringActive = true
    val activePermissions = listOf("Microphone", "Location", "Camera")
    val sensorEvents = 256
    val uiEvents = 120
    val correlations = 17

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Show start/stop monitoring dialog */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    if (monitoringActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (monitoringActive) Color(0xFF4CAF50) else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            if (monitoringActive) "Monitoring Active" else "Monitoring Inactive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "App is ${if (monitoringActive) "" else "not "}actively collecting sensor and UI data.",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Active Permissions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Row {
                            activePermissions.forEach {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.tertiary,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatColumn("Sensor Events", sensorEvents, Icons.Default.Timeline)
                    StatColumn("UI Events", uiEvents, Icons.Default.TouchApp)
                    StatColumn("Correlations", correlations, Icons.Default.CompareArrows)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Recent Correlations", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Divider(Modifier.padding(vertical = 8.dp))
                    Text("No recent correlations found.", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { /* Export Data logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Export Data")
                }
                OutlinedButton(
                    onClick = { /* Delete All Data logic */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    Spacer(Modifier.width(6.dp))
                    Text("Delete All", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun StatColumn(label: String, value: Int, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Text("$value", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 13.sp, color = Color.Gray)
    }
}