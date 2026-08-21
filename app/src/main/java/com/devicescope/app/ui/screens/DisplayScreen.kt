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
import com.devicescope.app.data.DisplayProvider
import com.devicescope.app.ui.components.InfoRow
import java.util.Locale

@Composable
fun DisplayScreen() {
    val context = LocalContext.current
    var disp by remember { mutableStateOf(DisplayProvider.collect(context)) }
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { disp = DisplayProvider.collect(context) }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.resolution), disp.resolution)
        InfoRow(stringResource(R.string.density), "${disp.densityDpi} dpi (${disp.densityName})")
        InfoRow(stringResource(R.string.refresh_rate), "${disp.refreshRateHz} Hz")
        InfoRow(stringResource(R.string.screen_size), "%.2f in".format(Locale.US, disp.screenSizeInches))
    }
}