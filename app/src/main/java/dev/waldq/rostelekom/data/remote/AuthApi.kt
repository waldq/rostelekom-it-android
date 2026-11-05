package dev.waldq.rostelekom.data.remote

import dev.waldq.rostelekom.models.OtpChallengeDto
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto
import dev.waldq.rostelekom.models.TokenPairDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/otp/request")
    suspend fun requestCode(@Body body: OtpRequestDto): OtpChallengeDto

    @POST("api/auth/otp/verify")
    suspend fun verifyCode(@Body body: OtpVerifyDto): TokenPairDto
}