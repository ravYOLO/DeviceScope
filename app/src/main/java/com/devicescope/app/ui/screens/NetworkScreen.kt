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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.devicescope.app.data.NetworkInfo
import com.devicescope.app.data.NetworkProvider
import com.devicescope.app.data.PingTester
import com.devicescope.app.ui.components.InfoRow
import com.devicescope.app.ui.components.SectionTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NetworkScreen() {
    val context = LocalContext.current
    var net by remember { mutableStateOf<NetworkInfo?>(null) }
    var pinging by remember { mutableStateOf(false) }
    var pingResult by remember { mutableStateOf(-1L) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        net = withContext(Dispatchers.Default) { NetworkProvider.collect(context) }
    }
    if (net == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = net!!
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { scope.launch(Dispatchers.Default) { net = NetworkProvider.collect(context) } }) {
                Text(stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.operator), data.operatorName.ifBlank { stringResource(R.string.unknown) })
        InfoRow(stringResource(R.string.network_type), data.networkType)
        InfoRow(stringResource(R.string.signal_strength), "${data.signalStrengthDbm} dBm")
        InfoRow(stringResource(R.string.ip_address), data.ipAddress.ifBlank { stringResource(R.string.unknown) })
        InfoRow("SIM", data.simCount.toString())
        InfoRow("Roaming", if (data.roaming) "✓" else "—")
        InfoRow(
            stringResource(R.string.wifi_info),
            if (data.wifiSsid.isBlank()) stringResource(R.string.unknown)
            else "${data.wifiSsid} (${data.wifiFrequencyMhz} MHz, ch ${data.wifiChannel})"
        )
        SectionTitle(stringResource(R.string.ping_test))
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    scope.launch {
                        pinging = true
                        pingResult = PingTester.measure(context)
                        pinging = false
                    }
                },
                enabled = !pinging
            ) {
                Text(stringResource(R.string.ping_test))
            }
            if (pinging) {
                CircularProgressIndicator()
            }
        }
        if (!pinging) {
            InfoRow(
                stringResource(R.string.ping_result),
                if (pingResult == -1L) stringResource(R.string.ping_timeout)
                else stringResource(R.string.ping_ms, pingResult)
            )
        }
    }
}