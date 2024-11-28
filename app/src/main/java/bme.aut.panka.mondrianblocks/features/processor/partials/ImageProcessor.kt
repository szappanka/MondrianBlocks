package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.ImageFormat
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import bme.aut.panka.mondrianblocks.GameData.INWARD_OFFSET_PERCENTAGE
import bme.aut.panka.mondrianblocks.GameData.initializedColors
import kotlinx.coroutines.CoroutineScope
import kotlin.math.sqrt

data class ProcessedResult(
    val bitmap: Bitmap?,
    val boundingRect: Rect? = null
)

interface ImageProcessor {
    fun process(bitmap: Bitmap?, rectangle: Rect?): ProcessedResult?

    // Az előző állapot tárolása
    var lastGridState: Array<Array<String?>>?

    // Az utolsó változatlan állapot időbélyege
    var lastUnchangedTime: Long

    var isColorCheckDone: Boolean

    fun hasGridStateChanged(newGridState: Array<Array<String?>>): Boolean {
        if (lastGridState == null) return true

        for (i in newGridState.indices) {
            for (j in newGridState[i].indices) {
                if (newGridState[i][j] != lastGridState!![i][j]) {
                    return true
                }
            }
        }
        return false
    }

    fun updateState(newGridState: Array<Array<String?>>) {
        if (hasGridStateChanged(newGridState)) {
            lastUnchangedTime = SystemClock.elapsedRealtime()
            lastGridState = newGridState
            isColorCheckDone = false
        }
    }

    /**
     * Ellenőrzi, hogy egy adott állapot x ideig változatlan-e
     */
    fun isStableForDuration(
        durationMillis: Long,
        gridState: Array<Array<String?>>
    ): Boolean {
        val elapsedMillis = SystemClock.elapsedRealtime() - lastUnchangedTime
        return elapsedMillis >= durationMillis
    }

    fun cropCenterToSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val newSize = minOf(width, height)

        val xOffset = (width - newSize) / 2
        val yOffset = (height - newSize) / 2

        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newSize, newSize)
    }

    fun calculateAverageColor(bitmap: Bitmap): IntArray {
        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        val pixelCount = bitmap.width * bitmap.height

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                redSum += Color.red(pixel)
                greenSum += Color.green(pixel)
                blueSum += Color.blue(pixel)
            }
        }

        return intArrayOf(
            redSum / pixelCount,
            greenSum / pixelCount,
            blueSum / pixelCount
        )
    }

    @OptIn(ExperimentalGetImage::class)
    fun processImageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null

        if (image.format != ImageFormat.YUV_420_888) {
            Log.e("ImageProcessor", "Invalid image format: ${image.format}")
            return null
        }

        val width = image.width
        val height = image.height
        val yBuffer = image.planes[0].buffer // Y plane
        val uBuffer = image.planes[1].buffer // U plane
        val vBuffer = image.planes[2].buffer // V plane
        val yRowStride = image.planes[0].rowStride
        val uvRowStride = image.planes[1].rowStride
        val uvPixelStride = image.planes[1].pixelStride

        val argbArray = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val yIndex = y * yRowStride + x
                val uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride

                val yValue = (yBuffer[yIndex].toInt() and 0xFF)
                val uValue = (uBuffer[uvIndex].toInt() and 0xFF) - 128
                val vValue = (vBuffer[uvIndex].toInt() and 0xFF) - 128

                val r = (yValue + 1.370705 * vValue).toInt().coerceIn(0, 255)
                val g = (yValue - 0.337633 * uValue - 0.698001 * vValue).toInt().coerceIn(0, 255)
                val b = (yValue + 1.732446 * uValue).toInt().coerceIn(0, 255)

                argbArray[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
            }
        }

        val bitmap = Bitmap.createBitmap(argbArray, width, height, Bitmap.Config.ARGB_8888)

        // Rotate the bitmap 90 degrees to correct orientation
        val matrix = Matrix().apply {
            postRotate(90f)
        }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Crop to center square
        return cropCenterToSquare(rotatedBitmap)
    }

    fun findClosestColorBGR(color: IntArray): String? {
        val distances = mutableMapOf<String, MutableList<Double>>()

        for ((colorName, colorList) in initializedColors) {
            val colorDistances = colorList.map { avgColor ->
                val deltaB = color[0] - avgColor[0]
                val deltaG = color[1] - avgColor[1]
                val deltaR = color[2] - avgColor[2]
                sqrt((deltaB * deltaB + deltaG * deltaG + deltaR * deltaR).toDouble())
            }
            if (colorDistances.isNotEmpty()) {
                distances[colorName] = colorDistances.toMutableList()
            }
        }

        if (distances.isEmpty()) return null

        val minDistances = distances.mapValues { it.value.minOrNull() ?: Double.MAX_VALUE }
        val closestEntry = minDistances.minByOrNull { it.value }
        //Log.d("ImageProcessor", "Closest color: ${closestEntry?.key}, distance: ${closestEntry?.value}")

        val threshold = 40.0

        if(closestEntry != null){
            if (((closestEntry.key == "BLACK" && closestEntry.value > threshold * 1.1) || closestEntry.key != "BLACK" && closestEntry.value > threshold)) {
                return null
            }
            return closestEntry.key
        }
        return null
    }

    fun processGridColors(
        bitmap: Bitmap,
        rectangle: Rect,
        findClosestColor: (IntArray) -> String?
    ): Array<Array<String?>> {
        val result = Array(8) { Array<String?>(8) { null } }

        val outerWidth = rectangle.width() / 8
        val outerHeight = rectangle.height() / 8

        val innerWidth = (outerWidth * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()
        val innerHeight = (outerHeight * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()

        val offsetX = (outerWidth * INWARD_OFFSET_PERCENTAGE / 2).toInt()
        val offsetY = (outerHeight * INWARD_OFFSET_PERCENTAGE / 2).toInt()

        for (col in 0 until 8) {
            for (row in 0 until 8) {
                val startX = rectangle.left + (col * outerWidth + (outerWidth - innerWidth) / 2).toInt()
                val startY = rectangle.top + (row * outerHeight + (outerHeight - innerHeight) / 2).toInt()

                val fieldRect = Rect(
                    startX + offsetX,
                    startY + offsetY,
                    startX + offsetX + innerWidth,
                    startY + offsetY + innerHeight
                )

                val croppedBitmap = cropCenterToSquare(
                    Bitmap.createBitmap(
                        bitmap,
                        fieldRect.left,
                        fieldRect.top,
                        fieldRect.width(),
                        fieldRect.height()
                    )
                )

                val averageColor = calculateAverageColor(croppedBitmap)
                val closestColorName = findClosestColor(averageColor)
                result[col][row] = closestColorName
            }
        }
        return result
    }
}
