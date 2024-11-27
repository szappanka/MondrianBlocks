package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import bme.aut.panka.mondrianblocks.GameData
import org.opencv.android.Utils
import org.opencv.core.Mat

import kotlinx.coroutines.*
import android.graphics.Rect
import bme.aut.panka.mondrianblocks.GameData.INWARD_OFFSET_PERCENTAGE
import bme.aut.panka.mondrianblocks.data.block.MondrianOrientation

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InitialisationProcessor : ImageProcessor {
    var onInitProcessed: (() -> Unit)? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    val deferredResult = CompletableDeferred<ProcessedResult?>()

    private val colorAverages = mutableMapOf<String, MutableList<IntArray>>()
    private var isProcessed = false
    private val processingMutex = Mutex()  // Add a Mutex to control access

    private var firstFieldRect: Rect? = null
    var newBitmap: Bitmap? = null

    override fun process(bitmap: Bitmap?, rectangle: Rect?): ProcessedResult? {
        // Check if already processed
        if (isProcessed) {
            deferredResult.complete(null)
            return null
        }

        // Only one coroutine can execute this block at a time
        coroutineScope.launch {
            processingMutex.withLock {
                if (isProcessed) return@withLock  // Double-check inside the lock

                bitmap?.let {
                    val mat = Mat()
                    Utils.bitmapToMat(it, mat)

                    val fieldWidth = rectangle?.width()!! / 8
                    val fieldHeight = rectangle.height() / 8

                    val innerFieldWidth = (fieldWidth * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()
                    val innerFieldHeight = (fieldHeight * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()

                    val offsetX = (fieldWidth * INWARD_OFFSET_PERCENTAGE / 2).toInt()
                    val offsetY = (fieldHeight * INWARD_OFFSET_PERCENTAGE / 2).toInt()

                    // Process the blocks in STARTER_PUZZLE
                    (GameData.STARTER_PUZZLE.blocks + GameData.STARTER_PUZZLE.blackBlocks).forEach { puzzleBlock ->
                        val outerIterator =
                            if (puzzleBlock.orientation == MondrianOrientation.HORIZONTAL)
                                puzzleBlock.block.width else puzzleBlock.block.height
                        val innerIterator =
                            if (puzzleBlock.orientation == MondrianOrientation.HORIZONTAL)
                                puzzleBlock.block.height else puzzleBlock.block.width

                        for (i in puzzleBlock.x until puzzleBlock.x + outerIterator) {
                            for (j in puzzleBlock.y until puzzleBlock.y + innerIterator) {

                                val fieldRect = Rect(
                                    rectangle.left + (i - 1) * fieldWidth + offsetX,
                                    rectangle.top + (j - 1) * fieldHeight + offsetY,
                                    rectangle.left + (i - 1) * fieldWidth + offsetX + innerFieldWidth,
                                    rectangle.top + (j - 1) * fieldHeight + offsetY + innerFieldHeight
                                )

                                val fieldBitmap = Bitmap.createBitmap(
                                    it,
                                    fieldRect.left,
                                    fieldRect.top,
                                    fieldRect.width(),
                                    fieldRect.height()
                                )
                                val averageRGBColor = calculateAverageColor(fieldBitmap)


                                colorAverages.getOrPut(puzzleBlock.block.color.name) { mutableListOf() }
                                    .apply {
                                        add(averageRGBColor)
                                    }
                            }
                        }
                    }

                    withContext(Dispatchers.Main) {
                        onInitProcessed?.invoke()
                        GameData.initializedColors.putAll(colorAverages)
                    }

                    mat.release()
                    isProcessed = true
                    deferredResult.complete(ProcessedResult(newBitmap, firstFieldRect))
                }
            }
        }
        return null
    }

    fun cleanup() {
        coroutineScope.cancel()
    }
}
