package com.devicescope.app.ui.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.tests.ScreenTestScreen
import com.devicescope.app.tests.VibrationTestSection
import com.devicescope.app.ui.components.SectionTitle

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