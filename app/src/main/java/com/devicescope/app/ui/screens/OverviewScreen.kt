package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.OverviewInfo
import com.devicescope.app.data.SystemInfoProvider
import com.devicescope.app.ui.components.InfoRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OverviewScreen() {
    val context = LocalContext.current
    var data by remember { mutableStateOf<OverviewInfo?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        data = withContext(Dispatchers.Default) { SystemInfoProvider.collect(context) }
    }
    if (data == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val info = data!!
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroCard(info, onRefresh = { scope.launch(Dispatchers.Default) { data = SystemInfoProvider.collect(context) } }) }
        item { OverviewDetailsCard(info) }
    }
}

@Composable
private fun HeroCard(data: OverviewInfo, onRefresh: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_phone),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = data.model,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = data.manufacturer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.align(Alignment.End).size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun OverviewDetailsCard(data: OverviewInfo) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(stringResource(R.string.soc), data.soc)
            InfoRow(stringResource(R.string.android_version), data.androidVersion)
            InfoRow(stringResource(R.string.api_level), data.apiLevel.toString())
            InfoRow(stringResource(R.string.ram_total), data.ramTotalGb)
            InfoRow(stringResource(R.string.storage_free), data.storageFreeGb)
            InfoRow(stringResource(R.string.battery_level), "${data.batteryLevelPct}%")
            InfoRow(stringResource(R.string.resolution), data.resolution)
            InfoRow(stringResource(R.string.refresh_rate), "${data.refreshRate} Hz")
            InfoRow(stringResource(R.string.network_type), data.networkType)
        }
    }
}