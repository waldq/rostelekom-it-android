package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.AccountDto
import dev.waldq.rostelekom.models.PagedModel
import dev.waldq.rostelekom.models.TransactionDto
import dev.waldq.rostelekom.models.UserProfileDto
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("api/users/me")
    suspend fun getProfile(): UserProfileDto

    @POST("/api/billing/accounts")
    suspend fun openAccount(): AccountDto

    @GET("/api/billing/accounts/me")
    suspend fun getMyAccount(): AccountDto

    @GET("/api/billing/accounts/me/transactions")
    suspend fun getMyTransactions(): PagedModel<TransactionDto>
}