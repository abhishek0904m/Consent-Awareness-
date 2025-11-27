package com.example.csuper.export

import org.json.JSONObject

object ExportJsonBuilder {

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

        val stats = JSONObject().apply {
            put("sensorEvents", sensorEvents)
            put("uiEvents", uiEvents)
            put("correlations", correlations)
        }
        root.put("stats", stats)

        // Phase 1 placeholders
        root.put("foregroundEvents", org.json.JSONArray())
        root.put("permissionUsage", org.json.JSONArray())

        return root.toString(2)
    }
}