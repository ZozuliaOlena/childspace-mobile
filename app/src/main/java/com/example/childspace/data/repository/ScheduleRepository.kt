package com.example.childspace.data.repository

import com.example.childspace.data.model.ScheduleDto
import com.example.childspace.network.ScheduleApiService

class ScheduleRepository(private val apiService: ScheduleApiService) {

    suspend fun getMySchedule(): Result<List<ScheduleDto>> {
        return try {
            val response = apiService.getMySchedule()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Помилка завантаження розкладу: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}