package bme.aut.panka.mondrianblocks.features.processor.partials

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import bme.aut.panka.mondrianblocks.ui.theme.MondrianYellow
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class HandRecognizer(private val context: Context) {
    private var gestureRecognizer: GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    private fun setupGestureRecognizer() {
        val baseOptionBuilder = BaseOptions.builder()
        baseOptionBuilder.setDelegate(Delegate.CPU)
        baseOptionBuilder.setModelAssetPath("gesture_recognizer.task")

        val baseOptions = baseOptionBuilder.build()
        val optionsBuilder = GestureRecognizer.GestureRecognizerOptions.builder()
            .setBaseOptions(baseOptions)
            .setMinHandDetectionConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setRunningMode(RunningMode.IMAGE)

        val options = optionsBuilder.build()
        gestureRecognizer = GestureRecognizer.createFromOptions(context, options)
    }

    fun recognizeImage(bitmap: Bitmap): GestureRecognizerResult? {
        val mpImage = BitmapImageBuilder(bitmap).build()
        return gestureRecognizer?.recognize(mpImage)
    }

    fun drawLandmarksOnBitmap(bitmap: Bitmap, landmarks: List<List<PointF>>): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = MondrianYellow.toArgb()
            strokeWidth = 5f
            style = Paint.Style.FILL
        }

        for (hand in landmarks) {
            for (landmark in hand) {
                canvas.drawCircle(landmark.x, landmark.y, 10f, paint)
            }
        }

        return mutableBitmap
    }
}