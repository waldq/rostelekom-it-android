package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.UpdateProfileDto
import dev.waldq.rostelekom.models.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {
    @GET("api/users/me")
    suspend fun getProfile(): UserProfileDto

    @PATCH("api/users/me")
    suspend fun updateProfile(@Body body: UpdateProfileDto): UserProfileDto
}