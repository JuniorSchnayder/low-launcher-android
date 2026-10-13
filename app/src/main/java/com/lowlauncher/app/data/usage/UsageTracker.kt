package com.lowlauncher.app.data.usage

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import java.util.Calendar

data class DailyUsageInfo(
    val totalScreenTimeMinutes: Long = 0,
    val hasPermission: Boolean = false
) {
    val formattedDuration: String
        get() {
            if (!hasPermission) return ""
            val hours = totalScreenTimeMinutes / 60
            val mins = totalScreenTimeMinutes % 60
            return if (hours > 0) "${hours}h ${mins}m hoje" else "${mins}m hoje"
        }
}

class UsageTracker(private val context: Context) {

    fun hasUsagePermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getTodayUsage(): DailyUsageInfo {
        if (!hasUsagePermission()) {
            return DailyUsageInfo(totalScreenTimeMinutes = 0, hasPermission = false)
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            ?: return DailyUsageInfo(0, true)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        var totalTimeMs: Long = 0
        stats?.forEach { stat ->
            totalTimeMs += stat.totalTimeInForeground
        }

        val totalMinutes = totalTimeMs / (1000 * 60)
        return DailyUsageInfo(totalScreenTimeMinutes = totalMinutes, hasPermission = true)
    }
}
