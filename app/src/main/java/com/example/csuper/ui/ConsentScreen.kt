@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.csuper.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun ConsentScreen(onConsentComplete: () -> Unit = {}) {
    // Toggle states for each permission
    var location by remember { mutableStateOf(false) }
    var microphone by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf(false) }
    var bodySensors by remember { mutableStateOf(false) }
    var accessibility by remember { mutableStateOf(false) }
    var usageStats by remember { mutableStateOf(false) }

    val anyPermissionSelected = location || microphone || camera || bodySensors || accessibility || usageStats

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Consent & Permissions") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enable the permissions you consent to:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            PermissionToggle("Location", location) { location = it }
            PermissionToggle("Microphone", microphone) { microphone = it }
            PermissionToggle("Camera", camera) { camera = it }
            PermissionToggle("Body Sensors", bodySensors) { bodySensors = it }
            PermissionToggle("Accessibility", accessibility) { accessibility = it }
            PermissionToggle("Usage Access", usageStats) { usageStats = it }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onConsentComplete,
                enabled = anyPermissionSelected
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun PermissionToggle(label: String, enabled: Boolean, onChanged: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, Modifier.weight(1f))
        Switch(
            checked = enabled,
            onCheckedChange = onChanged
        )
    }
}