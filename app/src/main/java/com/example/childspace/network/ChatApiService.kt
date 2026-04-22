package com.example.childspace.network

import com.example.childspace.data.model.ChatDto
import com.example.childspace.data.model.ChatMessageResponseDto
import com.example.childspace.data.model.SendMessageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @GET("api/Chat")
    suspend fun getAllChats(): Response<List<ChatDto>>

    @GET("api/Message/Chat/{chatId}")
    suspend fun getChatMessages(
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<List<ChatMessageResponseDto>>

    @POST("api/Message/send")
    suspend fun sendMessage(
        @Body request: SendMessageDto
    ): Response<ChatMessageResponseDto>
}