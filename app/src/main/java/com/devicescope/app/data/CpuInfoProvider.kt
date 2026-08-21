package com.devicescope.app.data

import android.os.Build
import java.io.File

object CpuInfoProvider {

    private const val CPU_USAGE_SAMPLE_MS = 300L
    private const val CPU_SYS_DIR = "/sys/devices/system/cpu"
    private const val KHZ_TO_MHZ = 1000L

    fun collect(): CpuInfo {
        val coreCount = coreCount()
        return CpuInfo(
            soc = socName(),
            abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown",
            cores = coreCount,
            maxFreqMhz = maxFreqMhz(),
            minFreqMhz = minFreqMhz(),
            perCoreFreqsMhz = perCoreFreqsMhz(coreCount),
            usagePct = cpuUsagePct()
        )
    }

    private fun socName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL
        } else {
            Build.HARDWARE
        }
    }

    private fun coreCount(): Int {
        val present = readSysFile("$CPU_SYS_DIR/present")
        if (present != null) {
            var count = 0
            for (range in present.split(",")) {
                val parts = range.trim().split("-")
                val end = parts.lastOrNull()?.toIntOrNull() ?: return fallbackCoreCount()
                val start = parts.firstOrNull()?.toIntOrNull() ?: end
                count += end - start + 1
            }
            if (count > 0) return count
        }
        return fallbackCoreCount()
    }

    private fun fallbackCoreCount(): Int {
        val cpuDirs = File(CPU_SYS_DIR).listFiles { file ->
            file.isDirectory && file.name.startsWith("cpu") && file.name.drop(3).toIntOrNull() != null
        }
        val counted = cpuDirs?.size ?: 0
        return if (counted > 0) counted else Runtime.getRuntime().availableProcessors()
    }

    private fun maxFreqMhz(): Long {
        val kHz = readSysFile("$CPU_SYS_DIR/cpu0/cpufreq/cpuinfo_max_freq")?.toLongOrNull() ?: return 0L
        return kHz / KHZ_TO_MHZ
    }

    private fun minFreqMhz(): Long {
        val kHz = readSysFile("$CPU_SYS_DIR/cpu0/cpufreq/cpuinfo_min_freq")?.toLongOrNull() ?: return 0L
        return kHz / KHZ_TO_MHZ
    }

    private fun perCoreFreqsMhz(coreCount: Int): List<Long> {
        return (0 until coreCount).map { index ->
            val kHz = readSysFile("$CPU_SYS_DIR/cpu$index/cpufreq/scaling_cur_freq")?.toLongOrNull() ?: return@map 0L
            kHz / KHZ_TO_MHZ
        }
    }

    private fun cpuUsagePct(): Double {
        val first = readCpuStat() ?: return 0.0
        if (runCatching { Thread.sleep(CPU_USAGE_SAMPLE_MS) }.isFailure) return 0.0
        val second = readCpuStat() ?: return 0.0
        val deltaTotal = second.first - first.first
        val deltaIdle = second.second - first.second
        if (deltaTotal <= 0L) return 0.0
        return 100.0 * (deltaTotal - deltaIdle) / deltaTotal
    }

    private fun readCpuStat(): Pair<Long, Long>? {
        val line = readSysFile("/proc/stat")?.lineSequence()?.firstOrNull { it.startsWith("cpu ") } ?: return null
        val fields = line.trim().split(Regex("\\s+")).drop(1)
        if (fields.size < 5) return null
        val numbers = fields.mapNotNull { it.toLongOrNull() }
        val total = numbers.sum()
        val idle = (numbers.getOrNull(3) ?: 0L) + (numbers.getOrNull(4) ?: 0L)
        return Pair(total, idle)
    }

    private fun readSysFile(path: String): String? {
        return runCatching { File(path).readText().trim() }.getOrNull()?.takeIf { it.isNotEmpty() }
    }
}