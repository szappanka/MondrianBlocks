package bme.aut.panka.mondrianblocks.features.results

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _fileList = MutableStateFlow<List<File>>(emptyList())
    val fileList: StateFlow<List<File>> = _fileList

    private val folderName = "MondrianResults"

    fun loadFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            val resultsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), folderName)

            if (resultsDir.exists() && resultsDir.isDirectory) {
                _fileList.value = resultsDir.listFiles { file -> file.extension == "txt" }?.toList() ?: emptyList()
            } else {
                _fileList.value = emptyList()
            }
        }
    }

    private fun getMimeType(path: String): String? {
        val extension = path.substringAfterLast('.', "")
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
    }


    fun openFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val mimeType = getMimeType(file.name) ?: "text/csv"

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("OpenFile", "Failed to open file: ${e.message}")
        }
    }


}