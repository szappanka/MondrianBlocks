package bme.aut.panka.mondrianblocks.features.menu.user

import android.util.Log
import androidx.lifecycle.ViewModel
import bme.aut.panka.mondrianblocks.network.ApiService
import bme.aut.panka.mondrianblocks.network.OAuthResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoglicaUserViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val username = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun onUsernameChanged(newName: String) {
        username.value = newName
    }

    fun onPasswordChanged(newBirth: String) {
        password.value = newBirth
    }

    val oauthResponse = MutableStateFlow<OAuthResponse?>(null)

    fun requestAccessToken(code: String, codeVerifier: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.requestToken(
                grantType = "authorization_code",
                code = code,
                redirectUri = "https://coglica.aut.bme.hu/cogniapp",
                clientId = "coglica-web-001-007",
                codeVerifier = codeVerifier
            )
            if (response.isSuccessful && response.body() != null) {
                oauthResponse.value = response.body()
            } else {
                Log.d("RequestToken", "Failed to request token: HTTP ${response.code()} ${response.message()}")
                if (response.errorBody() != null) {
                    Log.d("RequestTokenError", "Error body: ${response.errorBody()?.string()}")
                }
            }
        }
    }
}