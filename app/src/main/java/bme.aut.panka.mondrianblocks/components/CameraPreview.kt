package bme.aut.panka.mondrianblocks.components

import android.graphics.Bitmap
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
    bitmap: Bitmap?
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
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Composable
fun CanvasView(
    bitmap: Bitmap,
    rectangle: Rect,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        // Get PreviewView dimensions
        val viewWidth = size.width
        val viewHeight = size.height

        // Calculate scale factors based on the bitmap and PreviewView dimensions
        val widthScale = viewWidth / bitmap.width
        val heightScale = viewHeight / bitmap.height

        // Apply scaling to the rectangle's position and size
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
    }
}