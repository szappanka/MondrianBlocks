package bme.aut.panka.mondrianblocks.features.menu.difficulty

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton
import bme.aut.panka.mondrianblocks.components.MondrianFilter
import bme.aut.panka.mondrianblocks.components.MondrianPreview
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType

@Composable
fun FixedDifficultyView(
    onNextClick: () -> Unit, onBackClick: () -> Unit
) {
    val viewModel: FixedDifficultyViewModel = viewModel()
    val puzzles = viewModel.puzzles.collectAsState(initial = emptyList())
    val activePuzzleId = remember { mutableIntStateOf(-1) }
    val activeFilter = remember { mutableStateOf<PuzzleType?>(null) }
    val filteredPuzzles = if (activeFilter.value != null) {
        puzzles.value.filter { it.difficulty == activeFilter.value }
    } else {
        puzzles.value
    }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text("Válaszd ki a pályát!")

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MondrianFilter(selectedDifficulty = activeFilter.value, onDifficultySelected = {
                activeFilter.value = it
            })
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.height(375.dp),
        ) {
            items(filteredPuzzles) { puzzle ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            activePuzzleId.intValue = puzzle.id
                        }
                ) {
                    MondrianPreview(
                        puzzle = puzzle,
                        squareSize = 8.dp,
                        coloredBorderPadding = 5.dp,
                    )
                    Text(
                        text = "Pálya ${puzzle.id}",
                        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                        fontWeight = if (puzzle.id == activePuzzleId.intValue) FontWeight.Bold else FontWeight.Normal,
                        style = TextStyle(textDecoration = if (puzzle.id == activePuzzleId.intValue) TextDecoration.Underline else TextDecoration.None)
                    )
                }
            }
        }

        MondrianButton(
            onClick = {
                viewModel.savePuzzle(puzzles.value.first { it.id == activePuzzleId.intValue })
                onNextClick()
            },
            text = stringResource(R.string.next),
            enabled = activePuzzleId.intValue != -1
        )

        MondrianButton(
            onClick = onBackClick, text = stringResource(R.string.back)
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}