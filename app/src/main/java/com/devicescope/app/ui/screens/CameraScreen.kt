package com.devicescope.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.devicescope.app.data.CameraEntry
import com.devicescope.app.data.CameraProvider
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    var cams by remember { mutableStateOf<List<CameraEntry>?>(null) }
    LaunchedEffect(Unit) {
        cams = withContext(Dispatchers.Default) { CameraProvider.collect(context) }
    }
    if (cams == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val data = cams!!
    if (data.isEmpty()) {
        Text(stringResource(R.string.not_available), Modifier.padding(16.dp))
        return
    }
    LazyColumn(Modifier.fillMaxSize()) {
        items(data, key = { it.id }) { camera -> CameraCard(camera) }
    }
}

@Composable
private fun CameraCard(camera: CameraEntry) {
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(facingLabel(camera.facing), style = MaterialTheme.typography.titleSmall)
            Text(
                "${String.format(Locale.US, "%.1f", camera.megapixels)} MP · ${String.format(Locale.US, "%.0f", camera.fovDegrees)}°",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                camera.id,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun facingLabel(facing: String): String = when (facing.lowercase()) {
    "back" -> stringResource(R.string.camera_back)
    "front" -> stringResource(R.string.camera_front)
    else -> facing
}