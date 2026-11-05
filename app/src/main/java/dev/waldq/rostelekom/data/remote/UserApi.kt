package dev.waldq.rostelekom.data.remote

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.UpdateProfileDto
import dev.waldq.rostelekom.models.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {
    @GET("api/users/me")
    suspend fun getProfile(): UserProfileDto

    @PATCH("api/users/me")
    suspend fun updateProfile(@Body body: UpdateProfileDto): UserProfileDto
}