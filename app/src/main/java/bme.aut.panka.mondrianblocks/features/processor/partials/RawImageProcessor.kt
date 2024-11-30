package bme.aut.panka.mondrianblocks.features.processor.partials

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock

class RawImageProcessor : ImageProcessor {
    var onImageProcessed: ((Bitmap) -> Unit)? = null
    override var lastGridState: Array<Array<String?>>? = null
    override var lastUnchangedTime: Long = SystemClock.elapsedRealtime()
    override var isColorCheckDone: Boolean = false

    override fun process(bitmap: Bitmap?, rectangle: Rect?): ProcessedResult? {
        bitmap?.let {
            onImageProcessed?.invoke(it)
            return ProcessedResult(it, null)
        }
        return null
    }
}
