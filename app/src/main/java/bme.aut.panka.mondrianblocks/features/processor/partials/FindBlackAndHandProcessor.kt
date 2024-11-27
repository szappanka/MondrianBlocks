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

            val gridColors = processGridColors(
                it,
                rectangle ?: return null,
            ) { color -> findClosestColorBGR(color) }

            // TODO: ez innen úgyis kuka
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

            return ProcessedResult(bitmap = resultBitmap, boundingRect = rectangle)
        }
        return null
    }
}