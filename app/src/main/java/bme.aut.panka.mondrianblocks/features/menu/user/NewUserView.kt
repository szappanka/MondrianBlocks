package bme.aut.panka.mondrianblocks.features.menu.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import bme.aut.panka.mondrianblocks.components.MondrianButton
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.ui.theme.MondrianBlue
import bme.aut.panka.mondrianblocks.ui.theme.blue
import bme.aut.panka.mondrianblocks.ui.theme.white


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUserView(
    onNextClick: () -> Unit, onBackClick: () -> Unit
) {
    val viewModel: NewUserViewModel = viewModel()
    val name = remember { mutableStateOf(viewModel.name.value) }
    val birth = remember { mutableStateOf(viewModel.birth.value) }

    var showError = remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Új felhasználó adatai")
        TextField(
            value = name.value,
            onValueChange = {
                name.value = it
                viewModel.onNameChanged(it)
            },
            label = { Text("Név") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = birth.value,
                onValueChange = {
                    birth.value = it
                    viewModel.onBirthChanged(it)
                },
                label = { Text("Születési dátum (ÉÉÉÉ.HH.NN)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            showError.value = false
                        } else {
                            if (birth.value.isNotEmpty()) showError.value =
                                !viewModel.isValidBirth()

                        }
                    },
                shape = RoundedCornerShape(8.dp),
            )
            if (showError.value) {
                Text("Helytelen formátum! (ÉÉÉÉ.HH.NN)", color = Color.Red)
            }
        }

        MondrianButton(
            onClick = {
                viewModel.saveUser()
                onNextClick()
            },
            text = stringResource(R.string.saved_users),
            enabled = viewModel.isValidBirth() && name.value.isNotEmpty()

        )
        MondrianButton(
            onClick = onBackClick, text = stringResource(R.string.back)
        )
    }
}