package dev.waldq.rostelekom.data.repository

import dev.waldq.rostelekom.data.remote.safeApiCall
import dev.waldq.rostelekom.models.CreateSubscriptionDto
import dev.waldq.rostelekom.models.PageDto
import dev.waldq.rostelekom.models.SubscriptionDto
import dev.waldq.rostelekom.models.TariffCategory
import dev.waldq.rostelekom.models.TariffDto

class RemoteTariffsRepository(private val api: TariffsApi) : TariffsApi {
    override suspend fun getTariffs(
        category: TariffCategory?,
        page: Int,
        size: Int,
    ): PageDto<TariffDto> =
        safeApiCall { api.getTariffs(category, page, size) }


    override suspend fun getTariff(id: String): TariffDto =
        safeApiCall { api.getTariff(id) }

    override suspend fun subscribe(body: CreateSubscriptionDto): SubscriptionDto =
        safeApiCall { api.subscribe(body) }

    override suspend fun getMySubscriptions(
        page: Int,
        size: Int,
    ): PageDto<SubscriptionDto> =
        safeApiCall { api.getMySubscriptions() }

    override suspend fun getSubscription(id: String): SubscriptionDto =
        safeApiCall { api.getSubscription(id) }

    override suspend fun cancelSubscription(
        id: String,
        immediate: Boolean,
    ): SubscriptionDto =
        safeApiCall { api.cancelSubscription(id, immediate) }
}
