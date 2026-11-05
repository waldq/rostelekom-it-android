package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.data.remote.AuthApi
import dev.waldq.rostelekom.data.remote.safeApiCall
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto

class RemoteAuthRepository(private val api: AuthApi) : AuthApi {
    override suspend fun requestCode(body: OtpRequestDto) =
        safeApiCall { api.requestCode(body) }

    override suspend fun verifyCode(body: OtpVerifyDto) =
        safeApiCall { api.verifyCode(body) }
}