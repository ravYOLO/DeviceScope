package com.devicescope.app.data

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object BatteryProvider {

    private val defaults = BatteryInfo(
        levelPct = 0,
        tempC = 0.0,
        voltageMv = 0,
        isCharging = false,
        technology = "Li-ion",
        health = "unknown",
        status = "unknown"
    )

    @Suppress("DEPRECATION")
    fun collect(context: Context): BatteryInfo = runCatching {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (intent == null) defaults else fromIntent(intent)
    }.getOrDefault(defaults)

    private fun fromIntent(intent: Intent): BatteryInfo {
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100).coerceAtLeast(1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
        return BatteryInfo(
            levelPct = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) * 100 / scale,
            tempC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0,
            voltageMv = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0),
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL,
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion",
            health = healthOf(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)),
            status = statusOf(status)
        )
    }

    private fun healthOf(health: Int): String = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "good"
        BatteryManager.BATTERY_HEALTH_DEAD -> "dead"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "overheat"
        BatteryManager.BATTERY_HEALTH_COLD -> "cold"
        else -> "unknown"
    }

    private fun statusOf(status: Int): String = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
        BatteryManager.BATTERY_STATUS_FULL -> "full"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
        else -> "unknown"
    }
}