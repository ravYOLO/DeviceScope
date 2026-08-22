package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.SensorEntry
import com.devicescope.app.data.SensorProvider
import com.devicescope.app.ui.components.SectionTitle
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val sensorGroups = listOf(
    R.string.sensor_accelerometer,
    R.string.sensor_gyroscope,
    R.string.sensor_proximity,
    R.string.sensor_light,
    R.string.sensor_other
)

private fun sensorTypeLabel(type: String): Int = when {
    type.contains("accelerometer", ignoreCase = true) -> R.string.sensor_accelerometer
    type.contains("gyroscope", ignoreCase = true) -> R.string.sensor_gyroscope
    type.contains("proximity", ignoreCase = true) -> R.string.sensor_proximity
    type.contains("light", ignoreCase = true) -> R.string.sensor_light
    else -> R.string.sensor_other
}

@Composable
fun SensorsScreen() {
    val context = LocalContext.current
    var sensors by remember { mutableStateOf<List<SensorEntry>?>(null) }
    LaunchedEffect(Unit) {
        sensors = withContext(Dispatchers.Default) { SensorProvider.collect(context) }
    }
    if (sensors == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = sensors!!
    LazyColumn(Modifier.fillMaxSize()) {
        for (groupId in sensorGroups) {
            val groupSensors = data.filter { sensorTypeLabel(it.type) == groupId }
            if (groupSensors.isNotEmpty()) {
                item(key = "header_$groupId") { SectionTitle(stringResource(groupId)) }
                items(groupSensors, key = { it.name }) { sensor -> SensorCard(sensor) }
            }
        }
    }
}

@Composable
private fun SensorCard(sensor: SensorEntry) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(sensor.name, style = MaterialTheme.typography.titleSmall)
            Text(
                "${sensor.vendor} · ${String.format(Locale.US, "%.2f", sensor.powerMa)} mA · ${sensor.range}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}