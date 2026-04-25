package com.example.childspace.network

import com.example.childspace.data.model.ChatDto
import com.example.childspace.data.model.ChatMessageResponseDto
import com.example.childspace.data.model.MessageUpdateDto
import com.example.childspace.data.model.SendMessageDto
import com.example.childspace.data.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @PUT("api/Message/{id}")
    suspend fun updateMessage(
        @Path("id") messageId: String,
        @Body request: MessageUpdateDto
    ): Response<ChatMessageResponseDto>

    @DELETE("api/Message/{id}")
    suspend fun deleteMessage(
        @Path("id") messageId: String
    ): Response<Unit>

    @GET("api/chat/{id}/participants")
    suspend fun getChatParticipants(
        @Path("id") chatId: String
    ): Response<List<UserDto>>
}