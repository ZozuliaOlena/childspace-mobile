package com.example.childspace.data.model

data class LoginResponse(
    val token: String,
    val id: String,
    val email: String,
    val firstName: String,
    val roles: List<String>
)