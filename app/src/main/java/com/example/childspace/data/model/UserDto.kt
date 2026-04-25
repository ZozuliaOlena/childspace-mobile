package com.example.childspace.data.model

data class UserDto(
    val id: String,
    val email: String? = null,
    val firstName: String,
    val lastName: String,
    val centerName: String
)
