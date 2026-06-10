package com.example.childspace.data.repository

import com.example.childspace.data.local.TokenManager
import com.example.childspace.data.model.LoginRequest
import com.example.childspace.network.AuthApiService

class AuthRepository(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(request: LoginRequest): Result<Unit> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                val primaryRole = body.roles.firstOrNull() ?: "Parent"

                tokenManager.saveAuthData(body.token, primaryRole, body.id)

                Result.success(Unit)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Невірний email або пароль"
                    400 -> "Перевірте правильність введених даних"
                    else -> "Помилка входу: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearAuthData()
    }
}