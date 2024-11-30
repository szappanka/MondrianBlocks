package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class BlueMaskProcessor : ImageProcessor {
    var onImageProcessed: ((ProcessedResult) -> Unit)? = null
    override var lastGridState: Array<Array<String?>>? = null
    override var lastUnchangedTime: Long = SystemClock.elapsedRealtime()
    override var isColorCheckDone: Boolean = false

    override fun process(bitmap: Bitmap?, rectangle: Rect?): ProcessedResult? {
        bitmap?.let {
            val mat = Mat()
            Utils.bitmapToMat(it, mat)

            val hsvMat = Mat()
            Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_RGB2HSV)

            val lowerBlue = Scalar(100.0, 150.0, 50.0)
            val upperBlue = Scalar(140.0, 255.0, 255.0)

            val mask = Mat()
            Core.inRange(hsvMat, lowerBlue, upperBlue, mask)

            val contours = ArrayList<MatOfPoint>()
            Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

            // Calculate a single bounding rectangle encompassing all detected blue areas
            var minX = Int.MAX_VALUE
            var minY = Int.MAX_VALUE
            var maxX = Int.MIN_VALUE
            var maxY = Int.MIN_VALUE

            for (contour in contours) {
                val rect = Imgproc.boundingRect(contour)
                minX = minOf(minX, rect.x)
                minY = minOf(minY, rect.y)
                maxX = maxOf(maxX, rect.x + rect.width)
                maxY = maxOf(maxY, rect.y + rect.height)
            }

            val combinedRect = if (contours.isNotEmpty()) {
                Rect(minX, minY, maxX, maxY)
            } else {
                null // No contours found
            }

            // Create the result bitmap with the blue mask applied
            val maskedMat = Mat()
            Core.bitwise_and(mat, mat, maskedMat, mask)

            val resultBitmap = Bitmap.createBitmap(maskedMat.cols(), maskedMat.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(maskedMat, resultBitmap)

            // Release resources
            mat.release()
            hsvMat.release()
            mask.release()
            maskedMat.release()

            // Prepare the result with a single bounding rectangle
            val result = ProcessedResult(resultBitmap, combinedRect)
            return result
        }

        // Return null ProcessedResult if bitmap is null
        return ProcessedResult(null, null)
    }
}
