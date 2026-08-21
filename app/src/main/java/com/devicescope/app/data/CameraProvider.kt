package com.devicescope.app.data

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.roundToInt

object CameraProvider {

    fun collect(context: Context): List<CameraEntry> = runCatching {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.cameraIdList.mapNotNull { id -> cameraEntryOf(cameraManager, id) }
    }.getOrDefault(emptyList())

    private fun cameraEntryOf(cameraManager: CameraManager, id: String): CameraEntry? = runCatching {
        val characteristics = cameraManager.getCameraCharacteristics(id)
        CameraEntry(
            id = id,
            facing = facingOf(characteristics),
            megapixels = megapixelsOf(characteristics),
            fovDegrees = fovDegreesOf(characteristics)
        )
    }.getOrNull()

    private fun facingOf(characteristics: CameraCharacteristics): String = when (characteristics.get(CameraCharacteristics.LENS_FACING)) {
        CameraCharacteristics.LENS_FACING_FRONT -> "front"
        CameraCharacteristics.LENS_FACING_BACK -> "back"
        else -> "external"
    }

    private fun megapixelsOf(characteristics: CameraCharacteristics): Float {
        val size = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE) ?: return 0f
        val megapixels = size.width.toLong() * size.height.toLong() / 1e6
        return (megapixels * 10).roundToInt() / 10f
    }

    private fun fovDegreesOf(characteristics: CameraCharacteristics): Float {
        val focalLength = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)?.firstOrNull() ?: return 0f
        val physicalSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE) ?: return 0f
        if (focalLength <= 0f || physicalSize.width <= 0f) return 0f
        val halfAngleRadians = atan((physicalSize.width / 2f) / focalLength)
        return (halfAngleRadians * 2 * 180.0 / PI).toFloat()
    }
}