package com.example.csuper.export

import org.json.JSONObject

object ExportJsonBuilder {
    // Minimal JSON for Phase 1. We will expand this in later phases.
    fun buildMinimal(
        sensorEvents: Int,
        uiEvents: Int,
        correlations: Int,
        apiLevel: Int,
        appVersion: String
    ): String {
        val root = JSONObject()
        root.put("generatedAt", System.currentTimeMillis())
        root.put("apiLevel", apiLevel)
        root.put("version", appVersion)

        root.put("stats", JSONObject().apply {
            put("sensorEvents", sensorEvents)
            put("uiEvents", uiEvents)
            put("correlations", correlations)
        })

        // Placeholders for future phases
        root.put("foregroundEvents", emptyList<Any>())
        root.put("permissionUsage", emptyList<Any>())

        return root.toString(2)
    }
}
