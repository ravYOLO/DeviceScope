package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.devicescope.app.data.NetworkProvider
import com.devicescope.app.ui.components.InfoRow

@Composable
fun NetworkScreen() {
    val context = LocalContext.current
    var net by remember { mutableStateOf(NetworkProvider.collect(context)) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { net = NetworkProvider.collect(context) }) {
                Text(stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.operator), net.operatorName.ifBlank { stringResource(R.string.unknown) })
        InfoRow(stringResource(R.string.network_type), net.networkType)
        InfoRow(stringResource(R.string.signal_strength), "${net.signalStrengthDbm} dBm")
        InfoRow(stringResource(R.string.ip_address), net.ipAddress.ifBlank { stringResource(R.string.unknown) })
        InfoRow("SIM", net.simCount.toString())
        InfoRow("Roaming", if (net.roaming) "✓" else "—")
    }
}