package dev.waldq.rostelekom.data.remote

import dev.waldq.rostelekom.models.AccountDto
import dev.waldq.rostelekom.models.CreatePaymentDto
import dev.waldq.rostelekom.models.PaymentDto
import dev.waldq.rostelekom.models.TransactionPageDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BillingApi {
    @GET("api/billing/accounts/me")
    suspend fun getAccount(): AccountDto

    @GET("api/billing/accounts/me/transactions")       // ?page=0&size=5
    suspend fun getTransactions(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): TransactionPageDto

    @POST("api/billing/payments")
    suspend fun createPayment(
        @Header("Idempotency-Key") key: String,        // заголовок только для этого запроса
        @Body body: CreatePaymentDto,
    ): PaymentDto
}