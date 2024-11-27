package bme.aut.panka.mondrianblocks.features.menu.difficulty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton

@Composable
fun DifficultyMenuView(
    onFixedClick: () -> Unit,
    onRandomClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MondrianButton(
            onClick = onFixedClick,
            text = "Rögzített pálya"
        )
        MondrianButton(
            onClick = onRandomClick,
            text = "Véletlenszerű pálya"
        )
        MondrianButton(
            onClick = onBackClick,
            text = stringResource(R.string.back)
        )
    }
}