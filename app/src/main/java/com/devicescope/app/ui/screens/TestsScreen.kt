package com.devicescope.app.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.tests.ScreenTestScreen
import com.devicescope.app.tests.VibrationTestSection
import com.devicescope.app.ui.components.SectionTitle
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MicState { IDLE, RECORDING, PLAYING }

private const val ToneVolume = 100
private const val ToneDurationMs = 1000
private const val ToneReleaseDelayMs = 1200L
private const val RecordingDurationMs = 3000L
private const val MicFileName = "mic_test.3gp"

@Composable
fun TestsScreen() {
    var showFullScreen by remember { mutableStateOf(false) }
    if (showFullScreen) {
        ScreenTestScreen(onExit = { showFullScreen = false })
    } else {
        TestsMenu(onStartFullScreenTest = { showFullScreen = true })
    }
}

@Composable
private fun TestsMenu(onStartFullScreenTest: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionTitle(stringResource(R.string.test_screen))
        }
        item {
            TestModeCard(onStartTest = onStartFullScreenTest)
        }
        item {
            VibrationTestSection()
        }
        item {
            SoundTestSection()
        }
        item {
            MicTestSection()
        }
        item {
            TestHintText()
        }
    }
}

@Composable
private fun TestModeCard(onStartTest: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onStartTest, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.test_colors))
            }
            Button(onClick = onStartTest, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.test_touch))
            }
        }
    }
}

@Composable
private fun TestHintText() {
    Text(
        text = stringResource(R.string.tap_to_finish),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    )
}

@Composable
private fun SoundTestSection() {
    Button(onClick = {
        runCatching {
            val generator = ToneGenerator(AudioManager.STREAM_MUSIC, ToneVolume).apply {
                startTone(ToneGenerator.TONE_PROP_BEEP, ToneDurationMs)
            }
            Handler(Looper.getMainLooper()).postDelayed({ generator.release() }, ToneReleaseDelayMs)
        }
    }) {
        Text(stringResource(R.string.sound_test))
    }
}

@Composable
private fun MicTestSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var micState by remember { mutableStateOf(MicState.IDLE) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            scope.launch { startRecording(context) { micState = it } }
        } else {
            Toast.makeText(context, context.getString(R.string.mic_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }
    Button(
        onClick = {
            if (micState == MicState.IDLE) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        enabled = micState == MicState.IDLE
    ) {
        Text(stringResource(R.string.mic_test))
    }
    when (micState) {
        MicState.IDLE -> Unit
        MicState.RECORDING -> StatusText(stringResource(R.string.mic_recording))
        MicState.PLAYING -> StatusText(stringResource(R.string.mic_playing))
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    )
}

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
private suspend fun startRecording(context: Context, onState: (MicState) -> Unit) {
    onState(MicState.RECORDING)
    val outputFile = File(context.cacheDir, MicFileName)
    val recorder = runCatching {
        MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }.getOrNull()
    if (recorder == null) {
        onState(MicState.IDLE)
        return
    }
    delay(RecordingDurationMs)
    runCatching { recorder.stop() }
    recorder.release()
    onState(MicState.PLAYING)
    val player = runCatching {
        MediaPlayer().apply {
            setDataSource(outputFile.absolutePath)
            setOnCompletionListener { completed ->
                completed.release()
                onState(MicState.IDLE)
            }
            prepare()
            start()
        }
    }.getOrNull()
    if (player == null) {
        onState(MicState.IDLE)
    }
}