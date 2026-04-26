package com.example.childspace.data.repository

import com.example.childspace.data.model.UserProfileDto
import com.example.childspace.network.ProfileApiService
import retrofit2.Response

class ProfileRepository(private val apiService: ProfileApiService) {

    suspend fun getProfile(): Result<UserProfileDto> {
        return try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Помилка завантаження: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Перевірте підключення до інтернету"))
        }
    }

    suspend fun updateFcmToken(token: String): Response<Unit> {
        return apiService.updateFcmToken(token)
    }
}