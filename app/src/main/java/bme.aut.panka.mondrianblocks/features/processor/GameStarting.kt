package bme.aut.panka.mondrianblocks.features.processor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.components.MondrianPreview

@Composable
fun GameStarting() {
    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        MondrianPreview(
            puzzle = GameData.STARTER_PUZZLE,
            squareSize = 15.dp,
        )
        Text("Rakd ki a fenti pályát az inicializáláshoz és nyomd meg a KEZDÉS gombot!")
    }
}

@Preview
@Composable
fun GameStartingPreview() {
    GameStarting()
}