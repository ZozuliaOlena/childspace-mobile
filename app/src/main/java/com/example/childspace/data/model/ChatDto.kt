package com.example.childspace.data.model

data class ChatDto (
    val id: String,
    val name: String?,
    val createdAt: String,
    val participantsCount: Int,
    val lastMessage: ChatMessageResponseDto?
)