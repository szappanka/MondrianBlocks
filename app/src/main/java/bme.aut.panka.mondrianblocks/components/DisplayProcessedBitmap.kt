package bme.aut.panka.mondrianblocks.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun DisplayProcessedBitmap(bitmap: Bitmap?) {
    bitmap?.let {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Processed Image",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
