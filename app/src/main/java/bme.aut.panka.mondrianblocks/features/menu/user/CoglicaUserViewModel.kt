package bme.aut.panka.mondrianblocks.features.menu.user

import dagger.hilt.android.lifecycle.HiltViewModel
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import net.openid.appauth.*
import java.security.SecureRandom
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CoglicaUserViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val authService: AuthorizationService = AuthorizationService(application)

    // Beállítás az OAuth 2.0-hoz
    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://coglica.aut.bme.hu/auth/oauth2/authorize"), // Authorization endpoint
        Uri.parse("https://coglica.aut.bme.hu/auth/oauth2/token") // Token endpoint
    )

    private val clientId = "mondrianAndroid"
    private val redirectUri = Uri.parse("bme.aut.panka.mondrianblocks://oauth2redirect")

    fun createAuthorizationRequest(): AuthorizationRequest {
        Log.d("Panku", "Creating authorization request")
        val codeVerifier = generateCodeVerifier()
        Log.d("Panku", "Code verifier: $codeVerifier")
        val request = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
            .setCodeVerifier(codeVerifier)
            .setScopes("openid")
            .build()

        val result = authService.getAuthorizationRequestIntent(request)
        Log.d("Panku", "Authorization request created")
        Log.d("Panku", "Authorization result: ${result.data}")
        return request
    }

    fun exchangeToken(response: AuthorizationResponse, callback: (String?) -> Unit) {
        val code = response.authorizationCode
        Log.d("Panku", "Authorization Code: $code")

        val tokenRequest = response.createTokenExchangeRequest()
        authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
            if (tokenResponse != null) {
                callback(tokenResponse.accessToken)
            } else {
                Log.e("Panku", "Token exchange failed", exception)
                callback(null)
            }
        }
    }

    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(32)
        secureRandom.nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }
}
