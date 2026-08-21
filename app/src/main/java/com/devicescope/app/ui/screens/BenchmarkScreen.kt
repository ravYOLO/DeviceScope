package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.benchmark.BenchmarkRunner
import com.devicescope.app.ui.components.InfoRow
import kotlinx.coroutines.launch

@Composable
fun BenchmarkScreen() {
    var running by remember { mutableStateOf(false) }
    var cpuScore by remember { mutableStateOf<Int?>(null) }
    var ramScore by remember { mutableStateOf<Long?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = {
                scope.launch {
                    running = true
                    cpuScore = BenchmarkRunner.runCpu()
                    ramScore = BenchmarkRunner.runRam()
                    running = false
                }
            },
            enabled = !running
        ) {
            Text(stringResource(R.string.benchmark_run))
        }
        if (running) {
            RunningIndicator()
        }
        BenchmarkResultsCard(cpuScore = cpuScore, ramScore = ramScore)
    }
}

@Composable
private fun RunningIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.benchmark_run))
    }
}

@Composable
private fun BenchmarkResultsCard(cpuScore: Int?, ramScore: Long?) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            InfoRow(
                label = stringResource(R.string.score_cpu),
                value = cpuScore?.toString() ?: stringResource(R.string.unknown)
            )
            InfoRow(
                label = stringResource(R.string.score_ram),
                value = ramScore?.let { "$it MB" } ?: stringResource(R.string.unknown)
            )
        }
    }
}