package com.devicescope.app.tests

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import kotlin.math.roundToInt

private enum class TestMode { COLORS, TOUCH }

private const val MaxTouchPoints = 10
private const val VibrationDurationMs = 300L
private val TouchCircleRadius = 40.dp
private val TestColors = listOf(
    Color.Black, Color.White, Color.Red, Color.Green,
    Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta
)

@Composable
fun ScreenTestScreen(onExit: () -> Unit) {
    var mode by remember { mutableStateOf(TestMode.COLORS) }
    Column(Modifier.fillMaxSize()) {
        TestModeBar(mode, onModeChange = { mode = it }, onExit)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (mode) {
                TestMode.COLORS -> ColorTestArea(onExit)
                TestMode.TOUCH -> TouchTestArea(onExit)
            }
        }
    }
}

@Composable
private fun TestModeBar(mode: TestMode, onModeChange: (TestMode) -> Unit, onExit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = mode == TestMode.COLORS,
            onClick = { onModeChange(TestMode.COLORS) },
            label = { Text(stringResource(R.string.test_colors)) }
        )
        FilterChip(
            selected = mode == TestMode.TOUCH,
            onClick = { onModeChange(TestMode.TOUCH) },
            label = { Text(stringResource(R.string.test_touch)) }
        )
        Spacer(Modifier.weight(1f))
        Button(onClick = onExit) {
            Text(stringResource(R.string.ok))
        }
    }
}

@Composable
private fun ColorTestArea(onExit: () -> Unit) {
    var colorIndex by remember { mutableStateOf(0) }
    Box(
        Modifier
            .fillMaxSize()
            .background(TestColors[colorIndex])
            .pointerInput(Unit) {
                detectTapGestures {
                    if (colorIndex >= TestColors.lastIndex) onExit() else colorIndex++
                }
            }
    ) {
        if (colorIndex >= TestColors.lastIndex) {
            FinishOverlay(Modifier.align(Alignment.Center))
        }
        Button(
            onClick = onExit,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}

@Composable
private fun FinishOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(stringResource(R.string.tap_to_finish), color = Color.White)
    }
}

@Composable
private fun TouchTestArea(onExit: () -> Unit) {
    val touchPoints = remember { mutableStateListOf<Offset>() }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (touchPoints.size >= MaxTouchPoints) touchPoints.removeAt(0)
                    touchPoints.add(offset)
                }
            }
    ) {
        Canvas(Modifier.fillMaxSize()) {
            touchPoints.forEachIndexed { index, point ->
                drawCircle(color = Color.Blue, radius = TouchCircleRadius.toPx(), center = point)
            }
        }
        touchPoints.forEachIndexed { index, point ->
            Text(
                text = "${index + 1}",
                color = Color.White,
                modifier = Modifier.offset {
                    IntOffset(point.x.roundToInt() - 12, point.y.roundToInt() - 12)
                }
            )
        }
        Text(
            text = stringResource(R.string.tap_to_finish),
            modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
        )
        Button(
            onClick = onExit,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}

@Composable
fun VibrationTestSection() {
    val context = LocalContext.current
    Button(onClick = {
        context.getSystemService(Vibrator::class.java)
            ?.vibrate(VibrationEffect.createOneShot(VibrationDurationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    }) {
        Text(stringResource(R.string.test_vibration))
    }
}