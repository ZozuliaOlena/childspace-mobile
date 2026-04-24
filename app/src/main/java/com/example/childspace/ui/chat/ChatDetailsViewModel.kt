package com.example.childspace.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childspace.data.model.ChatMessageResponseDto
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

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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

    fun sendMessage(content: String) {
        val chatId = activeChatId ?: return
        viewModelScope.launch {
            try {
                if (_editingMessage.value != null) {
                    // Логіка оновлення
                    // repository.updateMessage(_editingMessage.value!!.id, content)
                    _editingMessage.value = null
                    loadMessages() // Тимчасово
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
                // repository.deleteMessage(messageId)
                _messages.value = _messages.value.filter { it.id != messageId }
            } catch (e: Exception) {
                Log.e("ChatDetailVM", "Помилка видалення: ${e.message}")
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