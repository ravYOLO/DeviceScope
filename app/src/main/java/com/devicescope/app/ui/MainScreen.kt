package com.devicescope.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devicescope.app.R
import com.devicescope.app.data.ReportBuilder
import com.devicescope.app.ui.screens.AppsScreen
import com.devicescope.app.ui.screens.BatteryScreen
import com.devicescope.app.ui.screens.BenchmarkScreen
import com.devicescope.app.ui.screens.CameraScreen
import com.devicescope.app.ui.screens.CpuScreen
import com.devicescope.app.ui.screens.DisplayScreen
import com.devicescope.app.ui.screens.MemoryScreen
import com.devicescope.app.ui.screens.NetworkScreen
import com.devicescope.app.ui.screens.OverviewScreen
import com.devicescope.app.ui.screens.PhoneScreen
import com.devicescope.app.ui.screens.SensorsScreen
import com.devicescope.app.ui.screens.SystemScreen
import com.devicescope.app.ui.screens.TestsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Tab(@StringRes val labelRes: Int) {
    OVERVIEW(R.string.tab_overview),
    PHONE(R.string.tab_phone),
    SYSTEM(R.string.tab_system),
    CPU(R.string.tab_cpu),
    MEMORY(R.string.tab_memory),
    BATTERY(R.string.tab_battery),
    DISPLAY(R.string.tab_display),
    NETWORK(R.string.tab_network),
    SENSORS(R.string.tab_sensors),
    APPS(R.string.tab_apps),
    CAMERA(R.string.tab_camera),
    TESTS(R.string.tab_tests),
    BENCHMARK(R.string.tab_benchmark)
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedTab by rememberSaveable { mutableStateOf(Tab.OVERVIEW) }
    Scaffold(
        topBar = {
            AppTopBar(
                onShare = {
                    scope.launch {
                        val text = withContext(Dispatchers.Default) { ReportBuilder.build(context) }
                        shareReport(context, text)
                    }
                },
                onCopy = {
                    scope.launch {
                        val text = withContext(Dispatchers.Default) { ReportBuilder.build(context) }
                        copyReport(context, text)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            TabContent(selectedTab)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppTopBar(onShare: () -> Unit, onCopy: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        actions = {
            IconButton(onClick = onShare) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share_report)
                )
            }
            IconButton(onClick = onCopy) {
                Icon(
                    painter = painterResource(R.drawable.ic_copy),
                    contentDescription = stringResource(R.string.copy_report)
                )
            }
        }
    )
}

@Composable
private fun TabBar(selectedTab: Tab, onTabSelected: (Tab) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Tab.entries) { tab ->
            FilterChip(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                label = { Text(stringResource(tab.labelRes)) }
            )
        }
    }
}

@Composable
private fun TabContent(tab: Tab) {
    when (tab) {
        Tab.OVERVIEW -> OverviewScreen()
        Tab.PHONE -> PhoneScreen()
        Tab.SYSTEM -> SystemScreen()
        Tab.CPU -> CpuScreen()
        Tab.MEMORY -> MemoryScreen()
        Tab.BATTERY -> BatteryScreen()
        Tab.DISPLAY -> DisplayScreen()
        Tab.NETWORK -> NetworkScreen()
        Tab.SENSORS -> SensorsScreen()
        Tab.APPS -> AppsScreen()
        Tab.CAMERA -> CameraScreen()
        Tab.TESTS -> TestsScreen()
        Tab.BENCHMARK -> BenchmarkScreen()
    }
}

private fun shareReport(context: Context, reportText: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, reportText)
    }
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_report)))
}

private fun copyReport(context: Context, reportText: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(
        ClipData.newPlainText(context.getString(R.string.app_name), reportText)
    )
    Toast.makeText(context, R.string.copy_report, Toast.LENGTH_SHORT).show()
}