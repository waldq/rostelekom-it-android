package dev.waldq.rostelekom.data.repository;

import dev.waldq.rostelekom.data.remote.safeApiCall
import dev.waldq.rostelekom.models.CreatePaymentDto
import dev.waldq.rostelekom.models.PagedModel
import dev.waldq.rostelekom.models.PaymentDto

class RemoteBillingRepository(private val api: BillingApi): BillingApi {
    override suspend fun getMyPayment(id: String): PaymentDto =
        safeApiCall { api.getMyPayment(id) }

    override suspend fun createPayment(
        key: String,
        body: CreatePaymentDto
    ): PaymentDto =
        safeApiCall { api.createPayment(key=key, body=body) }

    override suspend fun getMyPayments(): PagedModel<PaymentDto> = // TODO добавить количество платежей
        safeApiCall { api.getMyPayments() }

}
