package com.example.childspace.data.repository

import com.example.childspace.data.model.ChatDto
import com.example.childspace.data.model.ChatMessageResponseDto
import com.example.childspace.data.model.SendMessageDto
import com.example.childspace.network.ChatApiService
import com.example.childspace.network.SignalRManager
import kotlinx.coroutines.flow.SharedFlow
import retrofit2.Response

class ChatRepository(private val apiService: ChatApiService,
                     private val signalRManager: SignalRManager
) {
    suspend fun getAllChats(): Response<List<ChatDto>> {
        return apiService.getAllChats()
    }

    suspend fun getChatMessages(chatId: String, page: Int, pageSize: Int = 50): Response<List<ChatMessageResponseDto>> {
        return apiService.getChatMessages(chatId, page, pageSize)
    }

    suspend fun sendMessage(chatId: String, content: String): Response<ChatMessageResponseDto> {
        val request = SendMessageDto(chatId = chatId, content = content)
        return apiService.sendMessage(request)
    }
    val incomingMessages: SharedFlow<ChatMessageResponseDto> = signalRManager.incomingMessages

    fun connectToLiveChat() {
        signalRManager.connect()
    }

    fun joinChatGroup(chatId: String) {
        signalRManager.joinChatGroup(chatId)
    }

    fun disconnectFromLiveChat() {
        signalRManager.disconnect()
    }
}