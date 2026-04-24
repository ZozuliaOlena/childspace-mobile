package com.example.childspace.network

import com.example.childspace.data.local.TokenManager
import com.example.childspace.data.model.ChatMessageResponseDto
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.util.Log

class SignalRManager(private val tokenManager: TokenManager ) {
    private var hubConnection: HubConnection? = null

    private val _incomingMessages = MutableSharedFlow<ChatMessageResponseDto>(extraBufferCapacity = 10)
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val HUB_URL = "${NetworkConfig.BASE_URL}chatHub"

    fun connect() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            return
        }

        try{
            hubConnection = HubConnectionBuilder.create(HUB_URL)
                .withAccessTokenProvider(Single.defer{
                    val token = tokenManager.getToken() ?: ""
                    Single.just(token)
                }
                )
                .build()

            hubConnection?.on("ReceiveMessage", { message: ChatMessageResponseDto ->
                Log.d("SignalR", "Нове повідомлення отримано: ${message.content}")
                _incomingMessages.tryEmit(message)

            }, ChatMessageResponseDto::class.java)

            hubConnection?.start()?.blockingAwait()
            Log.d("SignalR", "Успішно підключено до ChatHub")
        } catch (e: Exception){
            Log.e("SignalR", "Помилка підключення до SignalR: ${e.message}")
        }
    }

    fun joinChatGroup(chatId: String) {
        try {
            if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                hubConnection?.invoke("JoinChat", chatId)
                Log.d("SignalR", "Успішно зайшли в групу чату: $chatId")
            }
        } catch (e: Exception) {
            Log.e("SignalR", "Помилка входу в групу: ${e.message}")
        }
    }

    fun disconnect() {
        if (hubConnection?.connectionState == HubConnectionState.CONNECTED){
            hubConnection?.stop()
            Log.d("SignalR", "Відключено від ChatHub")
        }
    }
}