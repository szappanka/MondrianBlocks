package bme.aut.panka.mondrianblocks.features.menu.difficulty

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.user.User
import bme.aut.panka.mondrianblocks.network.ApiService
import bme.aut.panka.mondrianblocks.network.CoglicaUser
import bme.aut.panka.mondrianblocks.network.RecommendedGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoglicaDifficultyViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _response = MutableStateFlow<String?>(null)
    val response: StateFlow<String?> get() = _response

    private val _user = MutableStateFlow<CoglicaUser?>(null)
    val user: StateFlow<CoglicaUser?> get() = _user

    private val _gameConfigurations = MutableStateFlow<List<RecommendedGame>>(emptyList())
    val gameConfigurations: StateFlow<List<RecommendedGame>> get() = _gameConfigurations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchUserData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = apiService.getUserPersonalData()
                Log.d("Panku", "User data response: $response")
                if (response.isSuccessful) {
                    val userData = response.body()
                    _user.value = userData
                    Log.d("Panku", "User data fetched: $userData")
                    userData?.username?.let { username ->
                        triggerGameAssignment(username)
                    }
                } else {
                    Log.e("Panku", "Error fetching user data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Panku", "Exception fetching user data: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun triggerGameAssignment(username: String) {
        try {
            val response = apiService.triggerGameAssignment()
            if (response.isSuccessful) {
                Log.d("Panku", "Game assignment triggered successfully")
                fetchGameConfigurations(username)
            } else {
                Log.e(
                    "Panku",
                    "Error triggering game assignment: ${response.errorBody()?.string()}"
                )
            }
        } catch (e: Exception) {
            Log.e("Panku", "Exception triggering game assignment: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchGameConfigurations(username: String) {
        try {
            val response = apiService.getGameConfigurations(gameId = "38", username = username)
            Log.d("Panku", "Game configurations response: $response")
            if (response.isSuccessful) {
                val configurations = response.body().orEmpty()
                _gameConfigurations.value = configurations
                Log.d("Panku", "Game configurations fetched: ${_gameConfigurations.value}")

                val firstConfig = configurations.firstOrNull()
                if (firstConfig != null) {
                    val configDetailResponse = apiService.getDetailedGameConfig(firstConfig.id.toString())
                    if (configDetailResponse.isSuccessful) {
                        val configDetail = configDetailResponse.body()
                        Log.d("Panku", "Detailed configuration for ${firstConfig.id}: $configDetail")
                        GameData.coglicaPuzzleId = firstConfig.id
                        GameData.selectedUser = User(
                            name = _user.value?.username.toString(),
                            birth = _user.value?.birthDate.toString(),
                        )
                        Log.d("Panku", "Saved coglica_puzzle_id: ${GameData.coglicaPuzzleId}")
                    } else {
                        Log.e(
                            "Panku",
                            "Error fetching detailed config for ${firstConfig.id}: ${
                                configDetailResponse.errorBody()?.string()
                            }"
                        )
                    }
                } else {
                    Log.d("Panku", "No configurations available to process.")
                }
            } else {
                Log.e(
                    "Panku",
                    "Error fetching game configurations: ${response.errorBody()?.string()}"
                )
            }
        } catch (e: Exception) {
            Log.e("Panku", "Exception fetching game configurations: ${e.localizedMessage}")
        }
    }
}
