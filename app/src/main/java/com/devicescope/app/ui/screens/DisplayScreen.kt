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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.devicescope.app.data.DisplayInfo
import com.devicescope.app.data.DisplayProvider
import com.devicescope.app.ui.components.InfoRow
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DisplayScreen() {
    val context = LocalContext.current
    var disp by remember { mutableStateOf<DisplayInfo?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        disp = withContext(Dispatchers.Default) { DisplayProvider.collect(context) }
    }
    if (disp == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = disp!!
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = { scope.launch(Dispatchers.Default) { disp = DisplayProvider.collect(context) } }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
            }
        }
        InfoRow(stringResource(R.string.resolution), data.resolution)
        InfoRow(stringResource(R.string.density), "${data.densityDpi} dpi (${data.densityName})")
        InfoRow(stringResource(R.string.refresh_rate), "${data.refreshRateHz} Hz")
        InfoRow(stringResource(R.string.screen_size), "%.2f in".format(Locale.US, data.screenSizeInches))
    }
}