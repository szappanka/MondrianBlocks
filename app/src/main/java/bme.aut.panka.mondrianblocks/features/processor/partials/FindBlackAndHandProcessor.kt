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

    var landmarks = mutableStateOf<List<List<PointF>>?>(null)

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

//            val result = handRecognizer.recognizeImage(bitmap)
//            result?.let { recognizerResult ->
//                val landmarks: List<List<NormalizedLandmark>> = recognizerResult.landmarks()
//                if (landmarks.isNotEmpty()) {
//                    Log.d("Hand", "Hand found")
//
//                    // Konvertálás PointF-re
//                    val androidLandmarks: List<List<PointF>> = landmarks.map { hand ->
//                        hand.map { landmark ->
//                            PointF(
//                                landmark.x() * bitmap.width, // Skálázás a bitmap szélességére
//                                landmark.y() * bitmap.height // Skálázás a bitmap magasságára
//                            )
//                        }
//                    }
//
//                    // Landmarkok kirajzolása
//                    processedBitmap = handRecognizer.drawLandmarksOnBitmap(bitmap, androidLandmarks)
//                }
//            }

            detectedLandmarks = detectAndReturnLandmarks(bitmap, handRecognizer)

            val resultBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(resultBitmap)
            val paint = android.graphics.Paint()

            val gridColors = processGridColors(
                it,
                rectangle ?: return null
            ) { color -> findClosestColorBGR(color) }

            updateState(gridColors)

            /*
            for (i in 0 until 8) {
                for (j in 0 until 8) {
                    val colorName = gridColors[i][j]
                    val blockColor = blockColors[colorName?.uppercase()]?.toArgb()
                        ?: MondrianGray.toArgb()

                    val fieldRect = Rect(
                        rectangle.left + i * (rectangle.width() / 8),
                        rectangle.top + j * (rectangle.height() / 8),
                        rectangle.left + (i + 1) * (rectangle.width() / 8),
                        rectangle.top + (j + 1) * (rectangle.height() / 8)
                    )
                    paint.color = blockColor
                    paint.style = android.graphics.Paint.Style.FILL
                    canvas.drawRect(
                        fieldRect.left.toFloat(),
                        fieldRect.top.toFloat(),
                        fieldRect.right.toFloat(),
                        fieldRect.bottom.toFloat(),
                        paint
                    )
                }
            }


             */

            if (isStableForDuration(
                    durationMillis = 2000,
                    gridState = gridColors
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
