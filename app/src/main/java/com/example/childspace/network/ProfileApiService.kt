package com.example.childspace.network

import com.example.childspace.data.model.UserProfileDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApiService {
    @GET("api/User/profile")
    suspend fun getUserProfile(): Response<UserProfileDto>
}