package bme.aut.panka.mondrianblocks.features.menu.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.R
import bme.aut.panka.mondrianblocks.components.MondrianButton
import bme.aut.panka.mondrianblocks.ui.theme.blue
import bme.aut.panka.mondrianblocks.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoglicaUserView(
    onNextClick: () -> Unit, onBackClick: () -> Unit
) {
    val viewModel: CoglicaUserViewModel = viewModel()
    val username = remember { mutableStateOf(viewModel.username.value) }
    val password = remember { mutableStateOf(viewModel.password.value) }

    val authorizationCode = "Code obtained from redirect"
    val codeVerifier = "Previously stored code verifier"


    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Coglica felhasználó adatai")
        TextField(
            value = username.value,
            onValueChange = {
                username.value = it
                viewModel.onUsernameChanged(it)
            },
            label = { Text("Felhasználónév") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = white,
//                focusedIndicatorColor = blue,
//            )
        )
        TextField(
            value = password.value,
            onValueChange = {
                password.value = it
                viewModel.onPasswordChanged(it)
            },
            label = { Text("Jelszó") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = white,
//                focusedIndicatorColor = blue,
//            ),
            visualTransformation = PasswordVisualTransformation()
        )
        MondrianButton(
            onClick = { viewModel.requestAccessToken(authorizationCode, codeVerifier) },
            text = "Bejelentkezés",
            enabled = true

        )
        MondrianButton(
            onClick = onBackClick, text = stringResource(R.string.back)
        )
    }
}