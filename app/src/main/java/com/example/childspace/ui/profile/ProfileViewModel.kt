package com.example.childspace.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childspace.data.model.UserProfileDto
import com.example.childspace.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserProfileDto? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

//    init {
//        loadProfileData()
//    }

    fun loadProfileData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getProfile()

            result.onSuccess { profileData ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    user = profileData
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Невідома помилка"
                )
            }
        }
    }

    fun updateFcmToken(fcmToken: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateFcmToken(fcmToken)
                if (response.isSuccessful) {
                    Log.d("FCM_DEBUG", "Токен успішно збережено в базі даних (через ViewModel)!")
                } else {
                    Log.e("FCM_DEBUG", "Помилка збереження токена: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FCM_DEBUG", "Exception при відправці токена: ${e.message}")
            }
        }
    }
}