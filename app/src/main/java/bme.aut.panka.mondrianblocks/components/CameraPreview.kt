package bme.aut.panka.mondrianblocks.components

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Rect
import bme.aut.panka.mondrianblocks.ui.theme.medium

@Composable
fun CameraPreview(
    onPreviewViewCreated: (PreviewView) -> Unit,
    rectangle: Rect?,
    bitmap: Bitmap?,
    landmarks: List<List<PointF>>? = null
) {
    val context = LocalContext.current
    Box {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            factory = {
                PreviewView(context).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    clipToOutline = true
                    onPreviewViewCreated(this)
                }
            }
        )
        if (rectangle != null && bitmap != null) {
            CanvasView(
                bitmap = bitmap,
                rectangle = rectangle,
                landmarks = landmarks,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}


@Composable
fun CanvasView(
    bitmap: Bitmap,
    rectangle: Rect,
    landmarks: List<List<PointF>>? = null,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val widthScale = canvasWidth / bitmap.width
        val heightScale = canvasHeight / bitmap.height

        val scaledLeft = rectangle.left * widthScale
        val scaledTop = rectangle.top * heightScale
        val scaledWidth = rectangle.width() * widthScale
        val scaledHeight = rectangle.height() * heightScale

        drawRect(
            color = medium,
            topLeft = androidx.compose.ui.geometry.Offset(
                x = scaledLeft.toFloat(),
                y = scaledTop.toFloat()
            ),
            size = androidx.compose.ui.geometry.Size(
                width = scaledWidth.toFloat(),
                height = scaledHeight.toFloat()
            ),
            style = Stroke(width = 10f)
        )

        landmarks?.forEach { handLandmarks ->
            handLandmarks.forEach { landmark ->
                val scaledX = landmark.x * widthScale
                val scaledY = landmark.y * heightScale

                if (scaledX in 0f..canvasWidth && scaledY in 0f..canvasHeight) {
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.White,
                        radius = 10f,
                        center = androidx.compose.ui.geometry.Offset(
                            x = scaledX.toFloat(),
                            y = scaledY.toFloat()
                        )
                    )
                }
            }
        }
    }
}

