package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Rect
import org.opencv.core.Rect as OpenCvRect

object RectConverter {
    fun convertRect(opencvRect: OpenCvRect): Rect {
        return Rect(
            opencvRect.x,
            opencvRect.y,
            opencvRect.x + opencvRect.width,
            opencvRect.y + opencvRect.height
        )
    }
}