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
import com.devicescope.app.ui.components.InfoRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NetworkScreen() {
    val context = LocalContext.current
    var net by remember { mutableStateOf<NetworkInfo?>(null) }
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
    }
}