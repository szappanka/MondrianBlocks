package bme.aut.panka.mondrianblocks.features.processor.partials

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.os.SystemClock
import android.util.Log
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType
import org.opencv.android.Utils
import org.opencv.core.Mat

class PuzzleMatchingProcessor(
    private var actualPuzzle: Puzzle?,
    private val updateActualPuzzle: (Puzzle) -> Unit,
    private val onGameFinished: () -> Unit,
    private val getPlayingStartTime: () -> Long?,
    val context: Context
) : ImageProcessor {
    override var lastGridState: Array<Array<String?>>? = null
    override var lastUnchangedTime: Long = SystemClock.elapsedRealtime()
    override var isColorCheckDone: Boolean = false

    val handRecognizer = HandRecognizer(context)
    private val blockRecognitionHelper = BlockRecognitionHelper(context)

    override fun process(bitmap: Bitmap?, rectangle: Rect?): ProcessedResult? {
        var detectedLandmarks: List<List<PointF>>? = null
        bitmap?.let {
            val mat = Mat()
            Utils.bitmapToMat(it, mat)

            detectedLandmarks = detectAndReturnLandmarks(bitmap, handRecognizer)

            val isHandDetected = !detectedLandmarks.isNullOrEmpty()

            val resultBitmap = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            val gridColors = processGridColors(
                it,
                rectangle ?: return null
            ) { color -> findClosestColorBGR(color) }

            updateState(gridColors, isHandDetected)

            if (isStableForDuration(
                    durationMillis = 1000,
                    gridState = gridColors,
                    isHandDetected = isHandDetected
                ) && !isColorCheckDone
            ) {
                val newPuzzle = recognizePuzzle(gridColors)
                isColorCheckDone = true
                if (isWinner(newPuzzle)) {
                    onGameFinished()
                } else {
                    updateActualPuzzle(newPuzzle)
                }
            }
            return ProcessedResult(bitmap = resultBitmap, boundingRect = rectangle, landmarks = detectedLandmarks)
        }
        return null
    }

    private fun recognizePuzzle(gridColors: Array<Array<String?>>): Puzzle {
        var solution = Puzzle(
            blocks = mutableListOf(),
            blackBlocks = GameData.selectedPuzzle?.blackBlocks ?: mutableListOf(),
            difficulty = GameData.selectedPuzzle?.difficulty ?: PuzzleType.NONE,
        )

        val whiteBlock = blockRecognitionHelper.recognizeWhiteBlock(gridColors)
        whiteBlock?.let {
            solution.blocks.add(it)
        }

        val yellowBlocks = blockRecognitionHelper.recognizeYellowBlocks(gridColors)
        solution.blocks.addAll(yellowBlocks)

        val redBlocks = blockRecognitionHelper.recognizeRedBlocks(gridColors)
        redBlocks?.let {
            solution.blocks.addAll(it)
        }

        val blueBlocks = blockRecognitionHelper.recognizeBlueBlocks(gridColors)
        blueBlocks?.let {
            solution.blocks.addAll(it)
        }


        return solution
    }

    private fun isWinner(puzzle: Puzzle): Boolean {
        return puzzle.blocks.size == 8
    }
}

