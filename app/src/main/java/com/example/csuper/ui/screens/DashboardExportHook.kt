package com.example.csuper.ui.screens

import android.os.Build
import androidx.compose.runtime.Composable
import com.example.csuper.export.ExportJsonBuilder
import com.example.csuper.ui.components.ExportButton

/**
 * Phase 1: Minimal hook to wire the Export button into your dashboard.
 * Replace the placeholder counts with your real values when available.
 */
@Composable
fun DashboardExportSection(
    sensorEvents: Int,
    uiEvents: Int,
    correlations: Int,
    appVersionProvider: () -> String = { "1.0" }
) {
    ExportButton(
        fileNameProvider = { "privacy-export-${System.currentTimeMillis()}.json" },
        mimeType = "application/json",
        buildContent = {
            ExportJsonBuilder.buildMinimal(
                sensorEvents = sensorEvents,
                uiEvents = uiEvents,
                correlations = correlations,
                apiLevel = Build.VERSION.SDK_INT,
                appVersion = appVersionProvider()
            )
        }
    )
}
