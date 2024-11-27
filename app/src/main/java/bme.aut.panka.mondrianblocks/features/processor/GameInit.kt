package bme.aut.panka.mondrianblocks.features.processor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.components.MondrianPreview
import bme.aut.panka.mondrianblocks.data.puzzle.Puzzle

@Composable
fun GameInit(puzzle: Puzzle) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        MondrianPreview(
            puzzle = puzzle,
            squareSize = 15.dp,
        )
        Text("Rakd ki az alábbi kezdő alakzatot! A játék elkezdődik utána automatikusan.")
    }
}