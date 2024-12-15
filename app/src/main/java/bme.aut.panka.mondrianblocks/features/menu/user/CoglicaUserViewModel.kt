package bme.aut.panka.mondrianblocks.features.menu.user

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import bme.aut.panka.mondrianblocks.network.CoglicaAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import net.openid.appauth.*
import javax.inject.Inject

@HiltViewModel
class CoglicaUserViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val authManager = CoglicaAuthManager(application)

    fun createAuthorizationRequest(): AuthorizationRequest {
        return authManager.createAuthorizationRequest()
    }

    fun handleAuthorizationResponse(
        intent: Intent,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        authManager.handleAuthorizationResponse(intent, onSuccess, onError)
    }
}
