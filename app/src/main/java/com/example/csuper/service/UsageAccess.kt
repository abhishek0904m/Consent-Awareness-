package com.example.csuper.service

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings

/**
 * Helper functions for Usage Access permission.
 */
object UsageAccess {

    /**
     * Checks whether the app has been granted Usage Access permission.
     * @param context The application context
     * @return true if usage access is granted, false otherwise
     */
    fun hasUsageAccess(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Returns an Intent that opens the Usage Access settings screen.
     * @param context The application context (optional, not used in basic intent)
     * @return Intent to open Settings.ACTION_USAGE_ACCESS_SETTINGS
     */
    fun usageAccessIntent(context: Context? = null): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }
}
