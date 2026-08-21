package com.devicescope.app.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object AppsProvider {

    @Suppress("DEPRECATION")
    fun collect(context: Context): List<AppEntry> = runCatching {
        val packageManager = context.packageManager
        packageManager.getInstalledApplications(0)
            .mapNotNull { applicationInfo -> appEntryOf(packageManager, applicationInfo) }
            .sortedWith(compareBy<AppEntry> { it.isSystem }.thenBy { it.label.lowercase() })
    }.getOrDefault(emptyList())

    @Suppress("DEPRECATION")
    private fun appEntryOf(packageManager: PackageManager, applicationInfo: ApplicationInfo): AppEntry? = runCatching {
        AppEntry(
            packageName = applicationInfo.packageName,
            label = packageManager.getApplicationLabel(applicationInfo).toString(),
            versionName = versionNameOf(packageManager, applicationInfo.packageName),
            isSystem = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
            sizeBytes = 0L
        )
    }.getOrNull()

    @Suppress("DEPRECATION")
    private fun versionNameOf(packageManager: PackageManager, packageName: String): String {
        return runCatching { packageManager.getPackageInfo(packageName, 0).versionName ?: "" }.getOrDefault("")
    }
}