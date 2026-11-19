package dev.waldq.rostelekom.models

import dev.waldq.rostelekom.models.serialization.Money
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

enum class TariffCategory {
    INTERNET,
    TV,
    MOBILE,
}

enum class BillingPeriod {
    MONTH
}

enum class SubscriptionStatus {
    ACTIVE,
    SUSPENDED,
    CANCELED,
}

@Serializable
data class TariffDto(
    val id: Uuid,
    val name: String,
    val description: String,
    val category: TariffCategory,
    val price: Money,
    val currency: String,
    val billingPeriod: BillingPeriod,
    val active: Boolean,
    val createdAt: String,
)

@Serializable
data class CreateSubscriptionDto(
    val tariffId: Uuid,
)

@Serializable
data class SubscriptionDto(
    val id: Uuid,
    val userId: Uuid,
    val tariffId: Uuid,
    val tariffName: String,
    val tariffCategory: TariffCategory,
    val status: SubscriptionStatus,
    val price: Money,
    val currency: String,
    val billingPeriod: BillingPeriod,
    val currentPeriodStart: String,
    val nextChargeAt: String,
    val cancelAtPeriodEnd: Boolean,
    val canceledAt: String? = null,
    val lastChargeTransactionId: Uuid? = null,
    val createdAt: String,
)
