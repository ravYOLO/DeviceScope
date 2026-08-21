package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.CpuInfoProvider
import com.devicescope.app.ui.components.InfoRow
import com.devicescope.app.ui.components.SectionTitle
import java.util.Locale

@Composable
fun CpuScreen() {
    var cpu by remember { mutableStateOf(CpuInfoProvider.collect()) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { cpu = CpuInfoProvider.collect() }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.soc), cpu.soc)
        InfoRow(stringResource(R.string.cores), cpu.cores.toString())
        InfoRow(stringResource(R.string.max_freq), "${cpu.maxFreqMhz} MHz")
        InfoRow(stringResource(R.string.min_freq), "${cpu.minFreqMhz} MHz")
        InfoRow(stringResource(R.string.abi), cpu.abi)
        InfoRow(stringResource(R.string.cpu_usage), "%.1f%%".format(Locale.US, cpu.usagePct))
        LinearProgressIndicator(
            progress = { (cpu.usagePct / 100.0).toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
        SectionTitle(stringResource(R.string.cpu_load_cores))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(cpu.perCoreFreqsMhz) { index, freqMhz ->
                CoreFreqCard(index, freqMhz)
            }
        }
    }
}

@Composable
private fun CoreFreqCard(index: Int, freqMhz: Long) {
    Card {
        Text(
            text = "C$index: $freqMhz MHz",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(12.dp)
        )
    }
}