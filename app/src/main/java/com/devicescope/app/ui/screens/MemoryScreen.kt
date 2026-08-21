package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.MemoryProvider
import com.devicescope.app.ui.components.InfoRow
import java.util.Locale

private const val KB_IN_MB = 1024L
private const val BYTES_IN_GB = 1024L * 1024L * 1024L

@Composable
fun MemoryScreen() {
    val context = LocalContext.current
    var mem by remember { mutableStateOf(MemoryProvider.collect(context)) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { mem = MemoryProvider.collect(context) }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        val ramProgress = if (mem.ramTotalMb > 0) mem.ramUsedMb / mem.ramTotalMb.toFloat() else 0f
        LinearProgressIndicator(progress = { ramProgress }, modifier = Modifier.fillMaxWidth())
        InfoRow(stringResource(R.string.ram_total), formatMb(mem.ramTotalMb))
        InfoRow(stringResource(R.string.ram_used), formatMb(mem.ramUsedMb))
        InfoRow(stringResource(R.string.ram_available), formatMb(mem.ramAvailableMb))
        val storageProgress = if (mem.storageTotalBytes > 0) mem.storageFreeBytes / mem.storageTotalBytes.toFloat() else 0f
        LinearProgressIndicator(progress = { storageProgress }, modifier = Modifier.fillMaxWidth())
        InfoRow(stringResource(R.string.storage_total), formatGb(mem.storageTotalBytes))
        InfoRow(stringResource(R.string.storage_free), formatGb(mem.storageFreeBytes))
    }
}

private fun formatMb(mb: Long): String {
    return if (mb > KB_IN_MB) "%.1f GB".format(Locale.US, mb / KB_IN_MB.toDouble()) else "$mb MB"
}

private fun formatGb(bytes: Long): String {
    return "%.1f GB".format(Locale.US, bytes / BYTES_IN_GB.toDouble())
}