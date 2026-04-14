package com.example.childspace.data.repository

import com.example.childspace.data.model.ScheduleDto
import com.example.childspace.network.ScheduleApiService

class ScheduleRepository(private val apiService: ScheduleApiService) {

    suspend fun getTeacherSchedule(): Result<List<ScheduleDto>> {
        return try {
            val response = apiService.getTeacherSchedule()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Помилка завантаження (Вчитель): ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChildrenSchedule(): Result<List<ScheduleDto>> {
        return try {
            val response = apiService.getChildrenSchedule()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Помилка завантаження (Батьки): ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}