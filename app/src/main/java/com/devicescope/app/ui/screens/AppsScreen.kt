package com.devicescope.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.AppEntry
import com.devicescope.app.data.AppsProvider
import com.devicescope.app.ui.components.InfoRow

@Composable
fun AppsScreen() {
    val context = LocalContext.current
    var apps by remember { mutableStateOf(AppsProvider.collect(context)) }
    val systemCount = apps.count { it.isSystem }
    val userApps = apps.filter { !it.isSystem }.take(200)
    LazyColumn(Modifier.fillMaxSize()) {
        item { InfoRow(stringResource(R.string.apps_count), apps.size.toString()) }
        item { InfoRow(stringResource(R.string.apps_system), systemCount.toString()) }
        item { InfoRow(stringResource(R.string.apps_user), (apps.size - systemCount).toString()) }
        items(userApps, key = { it.packageName }) { app -> AppRow(app) }
    }
}

@Composable
private fun AppRow(app: AppEntry) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                app.label.ifBlank { app.packageName }.first().uppercaseChar().toString(),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(app.label, style = MaterialTheme.typography.titleSmall)
            Text(
                app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}