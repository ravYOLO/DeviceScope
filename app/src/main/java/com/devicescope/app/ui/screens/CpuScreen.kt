package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.devicescope.app.data.CpuInfo
import com.devicescope.app.data.CpuInfoProvider
import com.devicescope.app.ui.components.InfoRow
import com.devicescope.app.ui.components.SectionTitle
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CpuScreen() {
    var cpu by remember { mutableStateOf<CpuInfo?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        cpu = withContext(Dispatchers.Default) { CpuInfoProvider.collect() }
    }
    if (cpu == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = cpu!!
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { scope.launch(Dispatchers.Default) { cpu = CpuInfoProvider.collect() } }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.soc), data.soc)
        InfoRow(stringResource(R.string.cores), data.cores.toString())
        InfoRow(stringResource(R.string.max_freq), "${data.maxFreqMhz} MHz")
        InfoRow(stringResource(R.string.min_freq), "${data.minFreqMhz} MHz")
        InfoRow(stringResource(R.string.abi), data.abi)
        InfoRow(stringResource(R.string.cpu_usage), "%.1f%%".format(Locale.US, data.usagePct))
        LinearProgressIndicator(
            progress = { (data.usagePct / 100.0).toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
        SectionTitle(stringResource(R.string.cpu_load_cores))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(data.perCoreFreqsMhz) { index, freqMhz ->
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