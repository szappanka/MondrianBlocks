package bme.aut.panka.mondrianblocks.features.results

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondianResultCard
import bme.aut.panka.mondrianblocks.components.MondrianButton
import java.io.File

@Composable
fun ResultsView(
    onBackClick: () -> Unit,
) {
    val viewModel: ResultsViewModel = viewModel()
    val fileListState = viewModel.fileList.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadFiles()
        Log.d("Panku", fileListState.value.toString())
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Results", modifier = Modifier.padding(bottom = 16.dp))

        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
        ){
            val fileList = fileListState.value
            items(fileList) { file ->
                MondianResultCard(
                    text = file.name,
                    onClick = { viewModel.openFile(context, file) }
                )
            }
        }

        MondrianButton(onClick = onBackClick, text = stringResource(R.string.back))
    }
}
