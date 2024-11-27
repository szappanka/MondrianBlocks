package bme.aut.panka.mondrianblocks.features.menu.user

import androidx.lifecycle.ViewModel
import bme.aut.panka.mondrianblocks.data.user.User
import bme.aut.panka.mondrianblocks.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import bme.aut.panka.mondrianblocks.GameData
import kotlinx.coroutines.launch

@HiltViewModel
class SavedUserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _users.value = users
            }
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            val user = userRepository.getUserById(id)
            GameData.selectedUser = user
        }
    }
}