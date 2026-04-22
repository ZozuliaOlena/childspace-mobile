package com.example.childspace.data.model

data class ChatMessageResponseDto(
    val id: String,
    val content: String,
    val createdAt: String,
    val senderId: String,
    val senderName: String
)