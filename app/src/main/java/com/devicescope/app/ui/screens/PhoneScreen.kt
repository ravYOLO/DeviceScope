package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.SystemInfoProvider
import com.devicescope.app.ui.components.InfoRow

@Composable
fun PhoneScreen() {
    val context = LocalContext.current
    val data = remember { SystemInfoProvider.collect(context) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow(stringResource(R.string.model), data.model)
                InfoRow(stringResource(R.string.manufacturer), data.manufacturer)
                InfoRow(stringResource(R.string.android_version), data.androidVersion)
                InfoRow(stringResource(R.string.security_patch), data.securityPatch)
                InfoRow(stringResource(R.string.resolution), data.resolution)
                InfoRow(stringResource(R.string.refresh_rate), "${data.refreshRate} Hz")
                InfoRow(stringResource(R.string.network_type), data.networkType)
                InfoRow(stringResource(R.string.battery_level), "${data.batteryLevelPct}%")
            }
        }
    }
}