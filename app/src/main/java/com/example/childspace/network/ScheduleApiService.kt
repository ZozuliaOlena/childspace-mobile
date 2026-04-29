package com.example.childspace.network

import com.example.childspace.data.model.ScheduleDto
import retrofit2.Response
import retrofit2.http.GET

interface ScheduleApiService {

    @GET("api/Schedule/my")
    suspend fun getMySchedule(): Response<List<ScheduleDto>>
}