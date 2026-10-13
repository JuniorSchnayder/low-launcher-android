package com.lowlauncher.app.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.lowlauncher.app.data.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    suspend fun getInstalledApps(
        favorites: Set<String>,
        frictionApps: Set<String>
    ): List<AppItem> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_ALL
        )

        val selfPackage = context.packageName

        resolveInfos
            .filter { it.activityInfo.packageName != selfPackage }
            .map { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val actName = resolveInfo.activityInfo.name
                val appLabel = resolveInfo.loadLabel(packageManager).toString()

                AppItem(
                    packageName = pkgName,
                    activityName = actName,
                    label = appLabel,
                    isFavorite = favorites.contains(pkgName),
                    hasFrictionDelay = frictionApps.contains(pkgName)
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(appItem: AppItem) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(appItem.packageName, appItem.activityName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        context.startActivity(intent)
    }

    fun isWhatsAppInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            try {
                context.packageManager.getPackageInfo("com.whatsapp.w4b", 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
    }
}
