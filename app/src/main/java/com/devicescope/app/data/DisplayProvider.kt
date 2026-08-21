package com.devicescope.app.data

import android.content.Context
import android.provider.Settings
import android.view.WindowManager
import kotlin.math.sqrt

object DisplayProvider {

    private val defaults = DisplayInfo(
        resolution = "0x0",
        densityDpi = 0,
        densityName = "unknown",
        refreshRateHz = 0,
        screenSizeInches = 0.0,
        currentBrightness = -1
    )

    fun collect(context: Context): DisplayInfo = runCatching {
        val metrics = context.resources.displayMetrics
        DisplayInfo(
            resolution = "${metrics.widthPixels}x${metrics.heightPixels}",
            densityDpi = metrics.densityDpi,
            densityName = densityNameOf(metrics.densityDpi),
            refreshRateHz = refreshRateOf(context),
            screenSizeInches = screenSizeInchesOf(metrics.widthPixels, metrics.heightPixels, metrics.xdpi, metrics.ydpi),
            currentBrightness = brightnessOf(context)
        )
    }.getOrDefault(defaults)

    @Suppress("DEPRECATION")
    private fun refreshRateOf(context: Context): Int = runCatching {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.refreshRate.toInt()
    }.getOrDefault(0)

    private fun screenSizeInchesOf(width: Int, height: Int, xdpi: Float, ydpi: Float): Double {
        val widthInches = (width / xdpi).toDouble()
        val heightInches = (height / ydpi).toDouble()
        return sqrt(widthInches * widthInches + heightInches * heightInches)
    }

    private fun densityNameOf(densityDpi: Int): String = when {
        densityDpi < 140 -> "ldpi"
        densityDpi < 200 -> "mdpi"
        densityDpi < 280 -> "hdpi"
        densityDpi < 360 -> "xhdpi"
        densityDpi < 560 -> "xxhdpi"
        densityDpi >= 560 -> "xxxhdpi"
        else -> "unknown"
    }

    private fun brightnessOf(context: Context): Int = runCatching {
        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, -1)
    }.getOrDefault(-1)
}