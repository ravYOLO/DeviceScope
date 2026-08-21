package com.devicescope.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.devicescope.app.ui.MainScreen
import com.devicescope.app.ui.theme.DeviceScopeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceScopeTheme {
                MainScreen()
            }
        }
    }
}