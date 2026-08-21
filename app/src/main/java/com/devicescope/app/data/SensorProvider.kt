package com.devicescope.app.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

object SensorProvider {

    fun collect(context: Context): List<SensorEntry> = runCatching {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getSensorList(Sensor.TYPE_ALL).map { sensor ->
            SensorEntry(
                name = sensor.name,
                vendor = sensor.vendor,
                type = typeNameOf(sensor.type),
                powerMa = sensor.power,
                range = sensor.maximumRange,
                resolution = sensor.resolution
            )
        }
    }.getOrDefault(emptyList())

    private fun typeNameOf(type: Int): String = when (type) {
        Sensor.TYPE_ACCELEROMETER -> "accelerometer"
        Sensor.TYPE_GYROSCOPE -> "gyroscope"
        Sensor.TYPE_PROXIMITY -> "proximity"
        Sensor.TYPE_LIGHT -> "light"
        Sensor.TYPE_MAGNETIC_FIELD -> "magnetometer"
        Sensor.TYPE_PRESSURE -> "pressure"
        Sensor.TYPE_STEP_COUNTER -> "step_counter"
        else -> "other"
    }
}