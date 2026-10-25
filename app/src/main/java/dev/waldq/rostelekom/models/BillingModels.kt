package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.math.BigDecimal

// TODO
enum class AccountStatus {
    ACTIVE,
    BLOCKED,
    CLOSED
}


/**
 * @property id UUID пользователя
 * @property number
 * @property balance баланс пользователя
 * @property status статус аккаунта
 * @property currency валюта аккаунта пользователя
 */
@Serializable
data class AccountDto(
    val id: String, // UUID на самом деле, просто в мобилке оно вряд ли будет использоваться
    val number: String,
    val balance: BigDecimal, // TODO либо написать сериализатор, либо заменить на строку
    val currency: String,
    val status: AccountStatus
)


/**
 * @property amount сумма
 *
 * Body запроса на сервер `/api/billing/payments`
 */
@Serializable
data class CreatePaymentDto(val amount: BigDecimal) // TODO то же самое