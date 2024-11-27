package bme.aut.panka.mondrianblocks.features.menu.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bme.aut.panka.mondrianblocks.GameData
import bme.aut.panka.mondrianblocks.data.user.User
import bme.aut.panka.mondrianblocks.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class NewUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val name = MutableStateFlow("")
    val birth = MutableStateFlow("")

    fun onNameChanged(newName: String) {
        name.value = newName
    }

    fun onBirthChanged(newBirth: String) {
        birth.value = newBirth
    }

    fun saveUser() {
        val user = User(name = name.value, birth = birth.value)
        viewModelScope.launch {
            val userId = userRepository.insert(user)
            val savedUser = userRepository.getUserById(userId.toString())
            GameData.selectedUser = savedUser
        }
    }

    fun isValidBirth(): Boolean {
        val pattern = Pattern.compile("^[0-9]{4}.[0-9]{2}.[0-9]{2}\$")
        return pattern.matcher(birth.value).matches()
    }
}