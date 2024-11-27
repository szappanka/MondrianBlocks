package bme.aut.panka.mondrianblocks.features.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.components.MondrianButton

@Composable
fun MainMenuView(
    onNewGameClick: () -> Unit, onResultsClick: () -> Unit, onExitClick: () -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MondrianButton(
            onClick = onNewGameClick, text = "Új játék"
        )
        MondrianButton(
            onClick = onResultsClick, text = "Eredmények"
        )
        MondrianButton(
            onClick = onExitClick, text = "Kilépés"
        )
    }
}