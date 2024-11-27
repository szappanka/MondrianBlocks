package bme.aut.panka.mondrianblocks.features.menu.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton
import bme.aut.panka.mondrianblocks.components.MondrianUserCard

@Composable
fun SavedUserView(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: SavedUserViewModel = viewModel()
    var users = viewModel.users.collectAsState(initial = emptyList())
    val activeUserId = remember { mutableIntStateOf(-1) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text(stringResource(R.string.saved_users))
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
        ){
            items(users.value.size) { index ->
                val user = users.value[index]
                MondrianUserCard(
                    name = user.name,
                    id = user.id,
                    birthDate = user.birth,
                    isActive = activeUserId.intValue == user.id,
                    setIsActive = { activeUserId.intValue = user.id }
                )
            }
        }

        MondrianButton(
            onClick = {
                viewModel.getUserById(activeUserId.intValue.toString())
                onNextClick()
            },
            text = stringResource(R.string.next),
            enabled = activeUserId.intValue != -1
        )
        MondrianButton(
            onClick = onBackClick,
            text = stringResource(R.string.back)
        )
    }
}