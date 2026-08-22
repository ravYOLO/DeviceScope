package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.BatteryInfo
import com.devicescope.app.data.BatteryProvider
import com.devicescope.app.ui.components.InfoRow
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val BatteryRefreshDelayMs = 5000L

@Composable
fun BatteryScreen() {
    val context = LocalContext.current
    var bat by remember { mutableStateOf<BatteryInfo?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        while (true) {
            bat = withContext(Dispatchers.Default) { BatteryProvider.collect(context) }
            delay(BatteryRefreshDelayMs)
        }
    }
    if (bat == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = bat!!
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { scope.launch(Dispatchers.Default) { bat = BatteryProvider.collect(context) } }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        Text(
            text = "${data.levelPct}%",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        LinearProgressIndicator(
            progress = { data.levelPct / 100f },
            modifier = Modifier.fillMaxWidth()
        )
        InfoRow(stringResource(R.string.battery_temp), "%.1f °C".format(Locale.US, data.tempC))
        InfoRow(stringResource(R.string.battery_voltage), "${data.voltageMv} mV")
        InfoRow(stringResource(R.string.battery_health), healthLabel(data.health))
        InfoRow(stringResource(R.string.battery_charging), data.status)
        InfoRow(stringResource(R.string.battery_technology), data.technology)
    }
}

@Composable
private fun healthLabel(health: String): String = when (health) {
    "good" -> stringResource(R.string.battery_health_good)
    "dead" -> stringResource(R.string.battery_health_dead)
    "overheat" -> stringResource(R.string.battery_health_overheat)
    "cold" -> stringResource(R.string.battery_health_cold)
    else -> stringResource(R.string.unknown)
}