package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.models.CreateSubscriptionDto
import dev.waldq.rostelekom.models.PageDto
import dev.waldq.rostelekom.models.SubscriptionDto
import dev.waldq.rostelekom.models.TariffCategory
import dev.waldq.rostelekom.models.TariffDto

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TariffsApi {
    @GET("api/services/tariffs")
    suspend fun getTariffs(
        @Query("category") category: TariffCategory? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PageDto<TariffDto>

    @GET("api/services/tariffs/{id}")
    suspend fun getTariff(@Path("id") id: String): TariffDto

    @POST("api/services/subscriptions")
    suspend fun subscribe(@Body body: CreateSubscriptionDto): SubscriptionDto

    @GET("api/services/subscriptions/my")
    suspend fun getMySubscriptions(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): PageDto<SubscriptionDto>

    @GET("api/services/subscriptions/{id}")
    suspend fun getSubscription(@Path("id") id: String): SubscriptionDto

    @POST("api/services/subscriptions/{id}/cancel")
    suspend fun cancelSubscription(
        @Path("id") id: String,
        @Query("immediate") immediate: Boolean = false
    ): SubscriptionDto
}