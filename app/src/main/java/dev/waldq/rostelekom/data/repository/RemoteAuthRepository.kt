package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.data.local.TokenStorage
import dev.waldq.rostelekom.data.remote.safeApiCall
import dev.waldq.rostelekom.models.OtpRequestDto
import dev.waldq.rostelekom.models.OtpVerifyDto
import dev.waldq.rostelekom.models.RegisterDto
import dev.waldq.rostelekom.models.VerifyResponseDto

class RemoteAuthRepository(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
    ) : AuthApi {
    override suspend fun requestCode(body: OtpRequestDto) =
        safeApiCall { api.requestCode(body) }

    override suspend fun verifyCode(body: OtpVerifyDto) =
        safeApiCall { api.verifyCode(body) }

    override suspend fun registerAccount(body: RegisterDto) =
        safeApiCall { api.registerAccount(body) }.also { saveTokens(it) }

    private suspend fun saveTokens(response: VerifyResponseDto) {
        if (response.accessToken != null && response.refreshToken != null) {
            tokenStorage.save(accessToken = response.accessToken, refreshToken = response.refreshToken)
        }
    }
}
