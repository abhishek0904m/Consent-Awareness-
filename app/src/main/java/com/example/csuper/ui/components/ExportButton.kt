package com.example.csuper.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Phase 1: Generic Export button that saves text content using SAF.
 * Pass a lambda that returns the file contents to write.
 */
@Composable
fun ExportButton(
    fileNameProvider: () -> String = { "privacy-export-${System.currentTimeMillis()}.json" },
    mimeType: String = "application/json",
    buildContent: () -> String
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(mimeType)
    ) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(context, "Export canceled", Toast.LENGTH_SHORT).show()
        } else {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val bytes = buildContent().toByteArray(Charsets.UTF_8)
                    os.write(bytes)
                    os.flush()
                }
                Toast.makeText(context, "Exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Button(onClick = { launcher.launch(fileNameProvider()) }) {
        Text("Export Data")
    }
}
