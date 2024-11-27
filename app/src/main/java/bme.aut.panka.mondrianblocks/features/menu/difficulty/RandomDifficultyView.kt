package bme.aut.panka.mondrianblocks.features.menu.difficulty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton
import bme.aut.panka.mondrianblocks.components.MondrianFilter
import bme.aut.panka.mondrianblocks.components.MondrianPreview
import bme.aut.panka.mondrianblocks.data.puzzle.PuzzleType

@Composable
fun RandomDifficultyView(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: RandomDifficultyViewModel = viewModel()
    val activeFilter = remember { mutableStateOf<PuzzleType?>(null) }
    val puzzle = viewModel.puzzle.collectAsState().value

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text("Válaszd ki a nehézséget!")

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MondrianFilter(selectedDifficulty = activeFilter.value, onDifficultySelected = {selected ->
                activeFilter.value = selected
                viewModel.getRandomPuzzle(selected)
            })
        }

        if (puzzle != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MondrianPreview(
                    puzzle = puzzle,
                    squareSize = 20.dp,
                    coloredBorderPadding = 5.dp,
                )
                Text(
                    text = "Pálya ${puzzle.id}",
                    modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                    fontWeight = FontWeight.Normal,
                )
            }
        }

        MondrianButton(
            onClick = {
                viewModel.savePuzzle()
                onNextClick()
            }
            , text = stringResource(R.string.saved_users),
        )

        MondrianButton(
            onClick = onBackClick, text = stringResource(R.string.back)
        )
    }
}