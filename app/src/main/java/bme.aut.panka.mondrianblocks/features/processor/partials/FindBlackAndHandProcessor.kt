package bme.aut.panka.mondrianblocks.features.processor.partials

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import bme.aut.panka.mondrianblocks.GameData
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import org.opencv.android.Utils
import org.opencv.core.Mat

class FindBlackAndHandProcessor(val context: Context) : ImageProcessor {

    override var lastGridState: Array<Array<String?>>? = null
    override var lastUnchangedTime: Long = SystemClock.elapsedRealtime()
    override var isColorCheckDone: Boolean = false
    var onAllBlackPlaced: () -> Unit = {}

    val handRecognizer = HandRecognizer(context)

    override fun process(
        bitmap: Bitmap?,
        rectangle: Rect?
    ): ProcessedResult? {
        var processedBitmap = bitmap
        var detectedLandmarks: List<List<PointF>>? = null

        bitmap?.let {
            val mat = Mat()
            Utils.bitmapToMat(it, mat)

            detectedLandmarks = detectAndReturnLandmarks(bitmap, handRecognizer)

            val isHandDetected = !detectedLandmarks.isNullOrEmpty()

            val gridColors = processGridColors(
                it,
                rectangle ?: return null
            ) { color -> findClosestColorBGR(color) }

            updateState(gridColors, isHandDetected)

            if (isStableForDuration(
                    durationMillis = 2000,
                    gridState = gridColors,
                    isHandDetected = isHandDetected
                ) && !isColorCheckDone
            ) {
                if (
                    gridColors.flatten().count { it == "BLACK" } == 6) {
                    if (checkStarterPuzzleMatch(gridColors)) {
                        onAllBlackPlaced()
                    }
                }
                isColorCheckDone = true
            }
            return ProcessedResult(bitmap = processedBitmap, boundingRect = rectangle, landmarks = detectedLandmarks)
        }
        return null
    }
}

private fun checkStarterPuzzleMatch(gridState: Array<Array<String?>>): Boolean {
    val puzzle = GameData.selectedPuzzle ?: return false

    val blackCoordinates = mutableSetOf<Pair<Int, Int>>()
    for (blackBlock in puzzle.blackBlocks) {
        for (xOffset in 0 until blackBlock.block.width) {
            for (yOffset in 0 until blackBlock.block.height) {
                val x = blackBlock.x + xOffset
                val y = blackBlock.y + yOffset
                blackCoordinates.add(Pair(x-1, y-1))
            }
        }
    }

    for (i in gridState.indices) {
        for (j in gridState[i].indices) {
            val isBlackRequired = Pair(i, j) in blackCoordinates

            if (isBlackRequired) {
                if (gridState[i][j] != "BLACK") {
                    return false
                }
            } else {
                if (gridState[i][j] != null) {
                    return false
                }
            }
        }
    }

    return true
}
