package bme.aut.panka.mondrianblocks.features.menu.user

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bme.aut.panka.mondrianblocks.components.MondrianButton
import net.openid.appauth.AuthorizationService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoglicaUserView(
    authCode : String? = null,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel: CoglicaUserViewModel = viewModel()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        Log.d("Panku", "Received activity result")
        data?.let { // Csak akkor folytatódik, ha a data nem null
            val response = AuthorizationResponse.fromIntent(it)
            val exception = AuthorizationException.fromIntent(it)

            if (response != null) {
                viewModel.exchangeToken(response) { token ->
                    if (token != null) {
                        Log.d("Panku", "Access Token: $token")
                    } else {
                        Log.d("Panku", "Failed to retrieve access token")
                    }
                }
            } else if (exception != null) {
                Log.d("Panku", "Authorization failed: ${exception.localizedMessage}")
            }
        } ?: run {
            Log.d("Panku", "Intent data was null")
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Text("Coglica felhasználó bejelentkezés")
        authCode?.let {
            Text("Kapott kód: $it")
        }
        MondrianButton(
            onClick = {
                val request = viewModel.createAuthorizationRequest()
                Log.d("Panku", "Starting authorization request")
                val authIntent = AuthorizationService(viewModel.getApplication()).getAuthorizationRequestIntent(request)
                Log.d("Panku", "Starting activity")
                launcher.launch(authIntent)
                Log.d("Panku", "Activity started")
            },
            text = "Bejelentkezés"
        )
        MondrianButton(
            onClick = onBackClick,
            text = "Vissza"
        )
    }
}
