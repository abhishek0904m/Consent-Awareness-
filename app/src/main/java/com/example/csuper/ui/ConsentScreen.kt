package com.example.csuper.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.csuper.R
import com.example.csuper.viewmodel.ConsentViewModel

/**
 * Consent Screen - First screen for users to grant permissions
 * Provides granular consent for each privacy-sensitive permission
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
    onConsentComplete: () -> Unit,
    viewModel: ConsentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.consent_title)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.consent_subtitle),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.consent_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Location Permission
            PermissionCard(
                title = stringResource(R.string.permission_location),
                description = stringResource(R.string.permission_location_desc),
                icon = Icons.Default.LocationOn,
                isGranted = uiState.locationConsent,
                onToggle = { viewModel.toggleLocationConsent() },
                onRequest = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Microphone Permission
            PermissionCard(
                title = stringResource(R.string.permission_microphone),
                description = stringResource(R.string.permission_microphone_desc),
                icon = Icons.Default.Mic,
                isGranted = uiState.microphoneConsent,
                onToggle = { viewModel.toggleMicrophoneConsent() },
                onRequest = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Camera Permission
            PermissionCard(
                title = stringResource(R.string.permission_camera),
                description = stringResource(R.string.permission_camera_desc),
                icon = Icons.Default.Camera,
                isGranted = uiState.cameraConsent,
                onToggle = { viewModel.toggleCameraConsent() },
                onRequest = {
                    permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sensors Permission
            PermissionCard(
                title = stringResource(R.string.permission_sensors),
                description = stringResource(R.string.permission_sensors_desc),
                icon = Icons.Default.Sensors,
                isGranted = uiState.sensorsConsent,
                onToggle = { viewModel.toggleSensorsConsent() },
                onRequest = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                        permissionLauncher.launch(arrayOf(Manifest.permission.BODY_SENSORS))
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Accessibility Service
            PermissionCard(
                title = stringResource(R.string.permission_accessibility),
                description = stringResource(R.string.permission_accessibility_desc),
                icon = Icons.Default.Accessibility,
                isGranted = uiState.accessibilityConsent,
                onToggle = { viewModel.toggleAccessibilityConsent() },
                onRequest = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Usage Stats Permission
            PermissionCard(
                title = stringResource(R.string.permission_usage_stats),
                description = stringResource(R.string.permission_usage_stats_desc),
                icon = Icons.Default.BarChart,
                isGranted = uiState.usageStatsConsent,
                onToggle = { viewModel.toggleUsageStatsConsent() },
                onRequest = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Privacy Statement
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PrivacyTip,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy Statement",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.consent_privacy_statement),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Continue Button
            Button(
                onClick = {
                    viewModel.completeConsent()
                    onConsentComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_continue))
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isGranted: Boolean,
    onToggle: () -> Unit,
    onRequest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isGranted) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = isGranted,
                    onCheckedChange = { onToggle() }
                )
                if (isGranted) {
                    TextButton(onClick = onRequest) {
                        Text("Settings", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
