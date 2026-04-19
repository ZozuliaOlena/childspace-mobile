package com.example.childspace.data.model

data class UserProfileDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val children: List<ChildProfileDto> = emptyList()
)