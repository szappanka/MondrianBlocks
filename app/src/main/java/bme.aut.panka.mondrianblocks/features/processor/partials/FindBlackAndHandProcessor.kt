package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import bme.aut.panka.mondrianblocks.GameData
import org.opencv.android.Utils
import org.opencv.core.Mat

class FindBlackAndHandProcessor : ImageProcessor {

    override var lastGridState: Array<Array<String?>>? = null
    override var lastUnchangedTime: Long = SystemClock.elapsedRealtime()
    override var isColorCheckDone: Boolean = false
    var onAllBlackPlaced: () -> Unit = {}

    override fun process(
        bitmap: Bitmap?,
        rectangle: Rect?
    ): ProcessedResult? {
        bitmap?.let {
            val mat = Mat()
            Utils.bitmapToMat(it, mat)

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
            }
            return ProcessedResult(bitmap = resultBitmap, boundingRect = rectangle)
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

    Log.d("Panku", "blackCoordinates: $blackCoordinates")
    Log.d("Panku", "gridState: ${gridState.mapIndexed { i, row -> row.mapIndexed { j, elem -> "$i, $j: $elem" } }}")

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
