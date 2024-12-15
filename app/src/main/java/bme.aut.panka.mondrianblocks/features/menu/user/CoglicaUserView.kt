package bme.aut.panka.mondrianblocks.features.menu.user

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.components.MondrianButton
import net.openid.appauth.AuthorizationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoglicaUserView(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: CoglicaUserViewModel = viewModel()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        data?.let { intent ->
            viewModel.handleAuthorizationResponse(intent,
                onSuccess = { accessToken ->
                    Log.d("Panku", "Access Token: $accessToken")
                    onNextClick()
                },
                onError = { error ->
                    Log.d("Panku", "Authorization failed: $error")
                }
            )
        } ?: run {
            Log.d("Panku", "Intent data was null")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Coglica User Login")
        MondrianButton(
            onClick = {
                val authService = AuthorizationService(viewModel.getApplication())
                val authIntent = authService.getAuthorizationRequestIntent(viewModel.createAuthorizationRequest())
                launcher.launch(authIntent)
            },
            text = "Coglica Bejelentkezés"
        )
        MondrianButton(
            onClick = onBackClick,
            // from string resources
            text = "Vissza"
        )
    }
}
