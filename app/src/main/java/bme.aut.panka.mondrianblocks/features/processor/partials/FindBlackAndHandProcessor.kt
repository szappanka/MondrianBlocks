package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import bme.aut.panka.mondrianblocks.GameData.INWARD_OFFSET_PERCENTAGE
import bme.aut.panka.mondrianblocks.ui.theme.MondrianGray
import bme.aut.panka.mondrianblocks.ui.theme.blockColors
import org.opencv.android.Utils
import org.opencv.core.Mat

class FindBlackAndHandProcessor : ImageProcessor {

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

            val outerWidth = rectangle?.width()!! / 8
            val outerHeight = rectangle.height() / 8

            val innerWidth = (outerWidth * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()
            val innerHeight = (outerHeight * (1 - INWARD_OFFSET_PERCENTAGE)).toInt()

            val offsetX = (outerWidth * INWARD_OFFSET_PERCENTAGE / 2).toInt()
            val offsetY = (outerHeight * INWARD_OFFSET_PERCENTAGE / 2).toInt()

            for (i in 0 until 8) {
                for (j in 0 until 8) {
                    val startX = rectangle.left + (i * outerWidth + (outerWidth - innerWidth) / 2).toInt()
                    val startY = rectangle.top + (j * outerHeight + (outerHeight - innerHeight) / 2).toInt()

                    val fieldRect = Rect(
                        startX + offsetX,
                        startY + offsetY,
                        startX + offsetX + innerWidth,
                        startY + offsetY + innerHeight
                    )

                    val color = calculateAverageColor(
                        cropCenterToSquare(
                            Bitmap.createBitmap(
                                it,
                                fieldRect.left,
                                fieldRect.top,
                                fieldRect.width(),
                                fieldRect.height()
                            )
                        )
                    )

                    val closestColorName = findClosestColorBGR(color)

                    val blockColor = blockColors[closestColorName?.uppercase()]?.toArgb()
                        ?: MondrianGray.toArgb()

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
            return ProcessedResult(bitmap = resultBitmap, boundingRect = rectangle)
        }
        return null
    }
}