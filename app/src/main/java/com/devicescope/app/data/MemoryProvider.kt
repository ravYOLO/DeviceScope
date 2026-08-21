package com.devicescope.app.data

import android.app.ActivityManager
import android.content.Context
import android.os.StatFs

object MemoryProvider {

    fun collect(context: Context): MemoryInfo {
        val memoryInfo = readMemoryInfo(context)
        val totalMb = bytesToMb(memoryInfo?.totalMem ?: 0L)
        val availableMb = bytesToMb(memoryInfo?.availMem ?: 0L)
        val usedMb = (totalMb - availableMb).coerceAtLeast(0L)
        val storage = readStorage(context)
        return MemoryInfo(
            ramTotalMb = totalMb,
            ramUsedMb = usedMb,
            ramAvailableMb = availableMb,
            storageTotalBytes = storage.first,
            storageFreeBytes = storage.second
        )
    }

    private fun readMemoryInfo(context: Context): ActivityManager.MemoryInfo? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager ?: return null
        val memoryInfo = ActivityManager.MemoryInfo()
        return runCatching {
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo
        }.getOrNull()
    }

    private fun readStorage(context: Context): Pair<Long, Long> {
        val statFs = runCatching { StatFs(context.filesDir.path) }.getOrNull() ?: return Pair(0L, 0L)
        val totalBytes = statFs.blockCountLong * statFs.blockSizeLong
        val freeBytes = statFs.availableBlocksLong * statFs.blockSizeLong
        return Pair(totalBytes, freeBytes)
    }

    private fun bytesToMb(bytes: Long): Long {
        return bytes / (1024L * 1024L)
    }
}