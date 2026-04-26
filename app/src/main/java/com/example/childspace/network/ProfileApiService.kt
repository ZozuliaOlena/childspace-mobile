package com.example.childspace.network

import com.example.childspace.data.model.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileApiService {
    @GET("api/User/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>

    @POST("api/User/fcm-token")
    suspend fun updateFcmToken(@Body token: String): Response<Unit>
}