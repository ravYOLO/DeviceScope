package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import com.devicescope.app.data.SystemInfo
import com.devicescope.app.data.SystemInfoProvider
import com.devicescope.app.ui.components.InfoRow
import com.devicescope.app.ui.components.SectionTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SystemScreen() {
    val context = LocalContext.current
    var sys by remember { mutableStateOf<SystemInfo?>(null) }
    LaunchedEffect(Unit) {
        sys = withContext(Dispatchers.Default) { SystemInfoProvider.collectSystem(context) }
    }
    if (sys == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = sys!!
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SystemSection("OS") {
            InfoRow(stringResource(R.string.android_version), data.osVersion)
            InfoRow(stringResource(R.string.api_level), data.apiLevel.toString())
            InfoRow(stringResource(R.string.security_patch), data.securityPatch)
            InfoRow(stringResource(R.string.build_number), data.buildNumber)
            InfoRow(stringResource(R.string.kernel), data.kernel)
            InfoRow(stringResource(R.string.uptime), data.uptime)
            InfoRow(stringResource(R.string.java_vm), data.javaVm)
        }
        SystemSection("Hardware") {
            InfoRow("Bootloader", data.bootloader)
            InfoRow("Hardware", data.hardware)
            InfoRow("Device", data.device)
            InfoRow("Product", data.product)
        }
    }
}

@Composable
private fun SystemSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionTitle(title)
            content()
        }
    }
}