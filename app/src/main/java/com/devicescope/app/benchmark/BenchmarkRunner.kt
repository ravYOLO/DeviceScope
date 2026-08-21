package com.devicescope.app.benchmark

import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CpuBenchmarkDurationMs = 2000L
private const val LoopIterationsPerRound = 100
private const val CpuScoreDivisor = 1000
private const val ChunkSizeMb = 1
private const val ChunkSizeBytes = ChunkSizeMb * 1024 * 1024
private const val MaxRamAllocationMb = 512

object BenchmarkRunner {

    suspend fun runCpu(): Int = withContext(Dispatchers.Default) {
        runCatching {
            val startTime = System.currentTimeMillis()
            var iterations = 0
            while (System.currentTimeMillis() - startTime < CpuBenchmarkDurationMs) {
                var x = 12345.678
                for (i in 0 until LoopIterationsPerRound) {
                    x = sqrt(x * 1.0001) + i
                }
                iterations += LoopIterationsPerRound
            }
            iterations / CpuScoreDivisor
        }.getOrDefault(0)
    }

    suspend fun runRam(): Long = withContext(Dispatchers.Default) {
        val chunks = mutableListOf<ByteArray>()
        var allocatedMb = 0L
        runCatching {
            while (allocatedMb < MaxRamAllocationMb) {
                chunks.add(ByteArray(ChunkSizeBytes))
                allocatedMb += ChunkSizeMb
            }
        }
        allocatedMb
    }
}