package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.data.remote.safeApiCall
import dev.waldq.rostelekom.models.AccountDto
import dev.waldq.rostelekom.models.PageDto
import dev.waldq.rostelekom.models.TransactionDto
import dev.waldq.rostelekom.models.UserProfileDto

class RemoteUserRepository(private val api: UserApi) : UserApi {

    override suspend fun getProfile(): UserProfileDto =
        safeApiCall { api.getProfile() }

    override suspend fun openAccount(): AccountDto =
        safeApiCall { api.openAccount() }


    override suspend fun getMyAccount(): AccountDto =
        safeApiCall { api.getMyAccount() }

    override suspend fun getMyTransactions(): PageDto<TransactionDto> =
        safeApiCall { api.getMyTransactions() }

}