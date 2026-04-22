package com.example.childspace.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childspace.data.model.ChatDto
import com.example.childspace.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatsViewModel(private val repository: ChatRepository): ViewModel(){
    private val _chats = MutableStateFlow<List<ChatDto>>(emptyList())
    val chats: StateFlow<List<ChatDto>> = _chats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadChats() {
        viewModelScope.launch{
            _isLoading.value = true
            try{
                val response = repository.getAllChats()
                if (response.isSuccessful){
                    val chatList = response.body() ?: emptyList()
                    _chats.value = chatList.sortedByDescending { it.createdAt }
                } else{
                    Log.e("ChatsVM", "Помилка завантаження чатів: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatsVM", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }

        }
    }
}