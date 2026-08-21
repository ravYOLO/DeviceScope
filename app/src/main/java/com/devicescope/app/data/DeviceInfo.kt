package com.devicescope.app.data

data class OverviewInfo(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val securityPatch: String,
    val apiLevel: Int,
    val soc: String,
    val ramTotalGb: String,
    val batteryLevelPct: Int,
    val storageFreeGb: String,
    val resolution: String,
    val refreshRate: Int,
    val networkType: String
)

data class SystemInfo(
    val osVersion: String,
    val apiLevel: Int,
    val buildNumber: String,
    val kernel: String,
    val uptime: String,
    val javaVm: String,
    val securityPatch: String,
    val bootloader: String,
    val hardware: String,
    val device: String,
    val product: String
)

data class CpuInfo(
    val soc: String,
    val abi: String,
    val cores: Int,
    val maxFreqMhz: Long,
    val minFreqMhz: Long,
    val perCoreFreqsMhz: List<Long>,
    val usagePct: Double
)

data class MemoryInfo(
    val ramTotalMb: Long,
    val ramUsedMb: Long,
    val ramAvailableMb: Long,
    val storageTotalBytes: Long,
    val storageFreeBytes: Long
)

data class BatteryInfo(
    val levelPct: Int,
    val tempC: Double,
    val voltageMv: Int,
    val health: String,
    val isCharging: Boolean,
    val technology: String,
    val status: String
)

data class DisplayInfo(
    val resolution: String,
    val densityDpi: Int,
    val densityName: String,
    val refreshRateHz: Int,
    val screenSizeInches: Double,
    val currentBrightness: Int
)

data class NetworkInfo(
    val operatorName: String,
    val networkType: String,
    val signalStrengthDbm: Int,
    val ipAddress: String,
    val simCount: Int,
    val roaming: Boolean
)

data class SensorEntry(
    val name: String,
    val vendor: String,
    val type: String,
    val powerMa: Float,
    val range: Float,
    val resolution: Float
)

data class AppEntry(
    val packageName: String,
    val label: String,
    val versionName: String,
    val isSystem: Boolean,
    val sizeBytes: Long
)

data class CameraEntry(
    val id: String,
    val facing: String,
    val megapixels: Float,
    val fovDegrees: Float
)