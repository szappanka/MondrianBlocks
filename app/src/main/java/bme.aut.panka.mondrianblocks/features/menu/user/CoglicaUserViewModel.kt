package bme.aut.panka.mondrianblocks.features.menu.user

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.openid.appauth.*
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import javax.inject.Inject

@HiltViewModel
class CoglicaUserViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val app = application
    private var authService: AuthorizationService? = null
    private var serviceConfiguration: AuthorizationServiceConfiguration? = null

    init {
        initializeAuth()
    }

    private fun initializeAuth() {
        val configUri = Uri.parse(AuthConfig.CONFIG_URI)
        AuthorizationServiceConfiguration.fetchFromUrl(configUri,
            { configuration, ex ->
                if (ex != null) {
                    Log.e("Panku", "Failed to fetch service configuration", ex)
                } else if (configuration != null) {
                    serviceConfiguration = configuration
                    authService = AuthorizationService(app)
                }
            }, DefaultConnectionBuilder.INSTANCE)
    }

    fun createAuthorizationRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.CALLBACK_URL.toUri()
        return serviceConfiguration?.let {
            AuthorizationRequest.Builder(
                it,
                AuthConfig.CLIENT_ID,
                AuthConfig.RESPONSE_TYPE,
                redirectUri
            )
                .setScope(AuthConfig.SCOPE)
                .build()
        } ?: throw IllegalStateException("Service configuration is not initialized")
    }

    fun handleAuthorizationResponse(intent: Intent, onSuccess: (String?) -> Unit, onError: (String?) -> Unit) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)
        Log.d("Panku", "Authorization response: $response")
        if (response != null) {
            authService?.performTokenRequest(response.createTokenExchangeRequest(),
            ) { tokenResponse, authException ->
                if (tokenResponse != null) {
                    val accessToken = tokenResponse.accessToken
                    saveToken(accessToken)
                    onSuccess(accessToken)
                } else if (authException != null) {
                    onError(authException.localizedMessage)
                }
            }
        } else if (exception != null) {
            onError(exception.localizedMessage)
        }
    }

    private fun saveToken(token: String?) {
        val sharedPreferences = app.getSharedPreferences("coglica_prefs", Application.MODE_PRIVATE)
        sharedPreferences.edit().putString("access_token", token).apply()
    }

    private fun getEndSessionRequest(idToken: String?): EndSessionRequest {
        return serviceConfiguration?.let {
            if (idToken.isNullOrEmpty()) {
                throw IllegalStateException("ID Token is null or empty")
            }
            EndSessionRequest.Builder(it)
                .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri())
                .setIdTokenHint(idToken)
                .build()
        } ?: throw IllegalStateException("Service configuration is null")
    }

    fun logout(idToken: String?, onLogoutComplete: () -> Unit, onError: (String) -> Unit) {
        try {
            // Létrehozza az EndSessionRequest-et
            val endSessionRequest = getEndSessionRequest(idToken)

            // Ellenőrzi, hogy az authService inicializálva van-e
            if (authService == null) {
                authService = AuthorizationService(app)
            }

            // Indítsd el a kijelentkezési folyamatot az intent segítségével
            val logoutIntent = authService?.getEndSessionRequestIntent(endSessionRequest)
            if (logoutIntent != null) {
                app.startActivity(logoutIntent) // Ez elindítja a böngészőt vagy megfelelő UI-t
                onLogoutComplete() // Jelzi, hogy a kijelentkezés elindult
            } else {
                onError("Logout intent is null")
            }

            // Törli a lokálisan tárolt tokent
            clearToken()

        } catch (e: Exception) {
            Log.e("CoglicaLogout", "Error during logout: ${e.localizedMessage}")
            onError(e.localizedMessage ?: "Unknown error during logout")
        }
    }

    private fun clearToken() {
        val sharedPreferences = app.getSharedPreferences("coglica_prefs", Application.MODE_PRIVATE)
        sharedPreferences.edit().remove("access_token").apply()
        sharedPreferences.edit().remove("id_token").apply()
    }


    private object AuthConfig {
        const val CONFIG_URI = "https://coglica.aut.bme.hu/auth/.well-known/openid-configuration"
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        const val SCOPE = "openid"
        const val CLIENT_ID = "mondrianAndroid"
        const val CALLBACK_URL = "bme.aut.panka.mondrianblocks://oauth2redirect"
        const val LOGOUT_CALLBACK_URL = "hu.bme.cognitiverpg://coglica.hu/postlogout"
    }
}
