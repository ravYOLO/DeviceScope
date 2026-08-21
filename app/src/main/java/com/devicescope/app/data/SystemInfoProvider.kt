package com.devicescope.app.data

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.StatFs
import android.os.SystemClock
import android.telephony.TelephonyManager
import android.view.WindowManager
import java.io.File
import java.util.Locale

object SystemInfoProvider {

    fun collect(context: Context): OverviewInfo {
        return OverviewInfo(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = androidVersion(),
            securityPatch = Build.VERSION.SECURITY_PATCH,
            apiLevel = Build.VERSION.SDK_INT,
            soc = socName(),
            ramTotalGb = ramTotalGb(context),
            batteryLevelPct = batteryLevelPct(context),
            storageFreeGb = storageFreeGb(context),
            resolution = resolution(context),
            refreshRate = refreshRateHz(context),
            networkType = networkTypeName(context)
        )
    }

    fun collectSystem(context: Context): SystemInfo {
        return SystemInfo(
            osVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildNumber = Build.DISPLAY,
            kernel = kernelVersion(),
            uptime = uptime(),
            javaVm = javaVm(),
            securityPatch = Build.VERSION.SECURITY_PATCH,
            bootloader = Build.BOOTLOADER,
            hardware = Build.HARDWARE,
            device = Build.DEVICE,
            product = Build.PRODUCT
        )
    }

    fun kernelVersion(): String {
        val line = readFile("/proc/version") ?: return "Unknown"
        return line.lineSequence().firstOrNull()?.trim() ?: "Unknown"
    }

    fun uptime(): String {
        val elapsedMs = SystemClock.elapsedRealtime()
        val days = elapsedMs / 86_400_000L
        val hours = (elapsedMs % 86_400_000L) / 3_600_000L
        val minutes = (elapsedMs % 3_600_000L) / 60_000L
        return "${days}d ${hours}h ${minutes}m"
    }

    fun javaVm(): String {
        val name = System.getProperty("java.vm.name") ?: "Unknown"
        val version = System.getProperty("java.vm.version") ?: ""
        return if (version.isBlank()) name else "$name $version"
    }

    private fun androidVersion(): String {
        val release = Build.VERSION.RELEASE
        val versionName = if (release.isBlank()) Build.VERSION.CODENAME else release
        return "$versionName (API ${Build.VERSION.SDK_INT})"
    }

    private fun socName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL
        } else {
            Build.HARDWARE
        }
    }

    private fun ramTotalGb(context: Context): String {
        val memoryInfo = readMemoryInfo(context) ?: return "Unknown"
        val totalGb = memoryInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f GB", totalGb)
    }

    private fun readMemoryInfo(context: Context): ActivityManager.MemoryInfo? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return null
        val memoryInfo = ActivityManager.MemoryInfo()
        return runCatching {
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo
        }.getOrNull()
    }

    private fun batteryLevelPct(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager ?: return 0
        return runCatching {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        }.getOrDefault(0).coerceAtLeast(0)
    }

    private fun storageFreeGb(context: Context): String {
        val statFs = runCatching { StatFs(context.filesDir.path) }.getOrNull() ?: return "Unknown"
        val freeBytes = statFs.availableBlocksLong * statFs.blockSizeLong
        val freeGb = freeBytes / (1024.0 * 1024.0 * 1024.0)
        return String.format(Locale.US, "%.1f GB", freeGb)
    }

    private fun resolution(context: Context): String {
        val metrics = context.resources.displayMetrics
        return "${metrics.widthPixels}x${metrics.heightPixels}"
    }

    @Suppress("DEPRECATION")
    private fun refreshRateHz(context: Context): Int {
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay
        }
        return runCatching { display?.refreshRate?.toInt() ?: 0 }.getOrDefault(0)
    }

    @SuppressLint("MissingPermission")
    private fun networkTypeName(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager ?: return "Unknown"
        val networkType = runCatching { telephonyManager.networkType }.getOrDefault(TelephonyManager.NETWORK_TYPE_UNKNOWN)
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_NR -> "5G"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSPA"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT -> "CDMA"
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO"
            else -> "Unknown"
        }
    }

    private fun readFile(path: String): String? {
        return runCatching { File(path).readText().trim() }.getOrNull()?.takeIf { it.isNotEmpty() }
    }
}