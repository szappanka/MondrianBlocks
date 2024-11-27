package bme.aut.panka.mondrianblocks.features.results

import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.stringResource
import androidx.room.util.TableInfo.Column
import bme.aut.panka.mondrianblocks.R

@Composable
fun ResultsView(
    onBackClick: () -> Unit
) {
    Column() {
        Text("Results")
        Button(onClick = onBackClick) {
            Text(stringResource(R.string.back))
        }
    }
}