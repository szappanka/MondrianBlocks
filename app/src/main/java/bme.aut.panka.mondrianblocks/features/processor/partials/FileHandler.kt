package bme.aut.panka.mondrianblocks.features.processor.partials

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException

class FileHandler(private val context: Context) {

    private val folderName = "MondrianResults"

    fun saveToFile(fileName: String, content: String) {
        try {
            val contentValues = ContentValues().apply {
                val epochTime = System.currentTimeMillis()
                val fixedFileName = if (fileName.endsWith(".txt")) {
                    fileName.substringBeforeLast(".txt") + "_$epochTime.txt"
                } else {
                    fileName + "_$epochTime.txt"
                }

                put(MediaStore.Downloads.DISPLAY_NAME, fixedFileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$folderName")
            }

            val uri: Uri? = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri).use { outputStream ->
                    outputStream?.write(content.toByteArray())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
