package bme.aut.panka.mondrianblocks.network

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import net.openid.appauth.*
import net.openid.appauth.connectivity.DefaultConnectionBuilder

class CoglicaAuthManager(private val app: Application) {

    private var authService: AuthorizationService? = null
    private var serviceConfiguration: AuthorizationServiceConfiguration? = null
    private var isInitialized = false
    private val initializationListeners = mutableListOf<() -> Unit>()

    init {
        initializeAuth()
    }

    private fun initializeAuth() {
        val configUri = Uri.parse(AuthConfig.CONFIG_URI)
        AuthorizationServiceConfiguration.fetchFromUrl(configUri,
            { configuration, ex ->
                if (ex != null) {
                    Log.e("CoglicaAuth", "Failed to fetch service configuration", ex)
                } else if (configuration != null) {
                    serviceConfiguration = configuration
                    authService = AuthorizationService(app)
                    isInitialized = true
                    notifyInitializationListeners()
                }
            }, DefaultConnectionBuilder.INSTANCE)
    }

    private fun notifyInitializationListeners() {
        initializationListeners.forEach { it() }
        initializationListeners.clear()
    }

    fun createAuthorizationRequest(): AuthorizationRequest {
        if (!isInitialized) {
            throw IllegalStateException("Service configuration is not initialized")
        }
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

    fun handleAuthorizationResponse(
        intent: Intent,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)
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

    fun logout(
        idToken: String?,
        onLogoutComplete: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val endSessionRequest = getEndSessionRequest(idToken)
            if (authService == null) {
                authService = AuthorizationService(app)
            }
            val logoutIntent = authService?.getEndSessionRequestIntent(endSessionRequest)
            if (logoutIntent != null) {
                app.startActivity(logoutIntent)
                onLogoutComplete()
            } else {
                onError("Logout intent is null")
            }
            clearToken()
        } catch (e: Exception) {
            Log.e("CoglicaLogout", "Error during logout: ${e.localizedMessage}")
            onError(e.localizedMessage ?: "Unknown error during logout")
        }
    }

    private fun clearToken() {
        val sharedPreferences = app.getSharedPreferences("coglica_prefs", Application.MODE_PRIVATE)
        sharedPreferences.edit().remove("access_token").apply()
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

    fun loadToken(): String? {
        val sharedPreferences = app.getSharedPreferences("coglica_prefs", Application.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
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