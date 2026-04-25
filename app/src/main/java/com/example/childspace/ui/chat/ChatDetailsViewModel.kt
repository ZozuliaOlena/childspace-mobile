package com.example.childspace.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.childspace.data.model.ChatMessageResponseDto
import com.example.childspace.data.model.UserDto
import com.example.childspace.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatDetailsViewModel (private val repository: ChatRepository) : ViewModel(){
    var activeChatId: String? = null
        private set
    private val _messages = MutableStateFlow<List<ChatMessageResponseDto>>(emptyList())
    val messages: StateFlow<List<ChatMessageResponseDto>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _participants = MutableStateFlow<List<UserDto>>(emptyList())
    val participants: StateFlow<List<UserDto>> = _participants.asStateFlow()

    private val _editingMessage = MutableStateFlow<ChatMessageResponseDto?>(null)
    val editingMessage: StateFlow<ChatMessageResponseDto?> = _editingMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.incomingMessages.collect { newMessage ->
                val currentList = _messages.value
                if (currentList.none { it.id == newMessage.id }) {
                    _messages.value = listOf(newMessage) + currentList
                }
            }
        }
    }

    fun openChat(chatId: String) {
        activeChatId = chatId
        _messages.value = emptyList()
        repository.connectToLiveChat()
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            repository.joinChatGroup(chatId)
        }
        loadMessages()
    }

    private fun loadMessages() {
        if (activeChatId == null) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Пагінацію додати
                val response = repository.getChatMessages(activeChatId!!, 1, 50)
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    _messages.value = list.sortedByDescending { it.createdAt }
                }
            } catch (e: Exception) {
                Log.e("ChatDetailVM", "Помилка: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadParticipants() {
        val chatId = activeChatId ?: return
        viewModelScope.launch{
            try{
                val response = repository.getChatParticipants(chatId)
                if (response.isSuccessful) {
                    _participants.value = response.body() ?: emptyList()
                } else {
                    Log.e("ChatDetailVM", "Помилка завантаження учасників: ${response.code()}")
                }
            } catch (e: Exception){
                Log.e("ChatDetailVM", "Exception: ${e.message}")
            }
        }
    }

    fun sendMessage(content: String) {
        val chatId = activeChatId ?: return
        viewModelScope.launch {
            try {
                if (_editingMessage.value != null) {
                    val messageId = _editingMessage.value!!.id
                    val response = repository.updateMessage(messageId, content)

                    if (response.isSuccessful){
                        _editingMessage.value = null
                        val updatedList = _messages.value.map { message ->
                            if (message.id == messageId) {
                                message.copy(content = content)
                            } else {
                                message
                            }
                        }
                        _messages.value = updatedList
                    } else{
                        Log.e("ChatDetailVM", "Помилка оновлення повідомлення: ${response.code()}")
                    }

                } else {
                    repository.sendMessage(chatId, content)
                }
            } catch (e: Exception) {
                Log.e("ChatDetailVM", "Помилка відправки: ${e.message}")
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            try {
                val response = repository.deleteMessage(messageId)
                if(response.isSuccessful){
                    _messages.value = _messages.value.filter { it.id != messageId }
                } else{
                    Log.e("ChatDetailVM", "Помилка видалення повідомлення: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatDetailVM", "Помилка під час видалення: ${e.message}")
            }
        }
    }
    fun setEditingMessage(message: ChatMessageResponseDto?) {
        _editingMessage.value = message
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectFromLiveChat()
    }
}