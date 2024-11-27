package bme.aut.panka.mondrianblocks.features.menu.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton

@Composable
fun UserMenuView(
    onNewUserClick: () -> Unit,
    onSaveUserClick: () -> Unit,
    onCoglicaUserClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MondrianButton(
            onClick = onNewUserClick,
            text = "Új játékos"
        )
        MondrianButton(
            onClick = onSaveUserClick,
            text = "Mentett játékos"
        )
        MondrianButton(
            onClick = onCoglicaUserClick,
            text = "Coglica felhasználó",
        )
        MondrianButton(
            onClick = onBackClick,
            text = stringResource(R.string.back)
        )
    }
}