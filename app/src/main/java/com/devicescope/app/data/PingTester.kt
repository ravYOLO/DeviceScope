package com.devicescope.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

object PingTester {

    private const val Attempts = 3
    private const val TimeoutMs = 2000
    private const val PingHost = "8.8.8.8"

    suspend fun measure(context: Context): Long = withContext(Dispatchers.IO) {
        (1..Attempts).mapNotNull { attemptOf() }.minOrNull() ?: 0L
    }

    private fun attemptOf(): Long? = runCatching {
        val startNanos = System.nanoTime()
        val reachable = InetAddress.getByName(PingHost).isReachable(TimeoutMs)
        if (reachable) (System.nanoTime() - startNanos) / 1_000_000 else null
    }.getOrNull()
}