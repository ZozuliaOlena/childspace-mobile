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

                // C# возвращает массив ролей. Берем первую роль.
                // Если массив пустой (чего быть не должно), ставим дефолт "Parent"
                val primaryRole = body.roles.firstOrNull() ?: "Parent"

                // Сохраняем токен и роль!
                tokenManager.saveAuthData(body.token, primaryRole, body.id)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Помилка входу: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        // Очищаем память при выходе
        tokenManager.clearAuthData()
    }
}