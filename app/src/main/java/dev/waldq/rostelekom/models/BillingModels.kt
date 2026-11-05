package dev.waldq.rostelekom.models

import kotlinx.serialization.Serializable

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
    val balance: String, // TODO либо написать сериализатор, либо заменить на строку
    val currency: String,
    val status: AccountStatus
)

enum class PaymentStatus {
    PENDING,         // создан, ждём оплату
    SUCCEEDED,       // деньги зачислены
    CANCELED,        // отменён или не прошёл
    REFUND_REQUIRED, // оплата пришла, когда счёт уже закрыт
}


@Serializable
data class TransactionPageDto(
    val content: List<TransactionDto>,
    val page: PageInfo,
)

enum class TransactionType {
    TOP_UP, // пополнение (+)
    CHARGE, // списание, например абонентская плата (-)
    REFUND, // возврат (+)
}


@Serializable
data class TransactionDto(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val balanceAfter: Double,
    val description: String? = null,
    val paymentId: String? = null,
    val createdAt: String,
)

/** Служебная часть страницы: Spring кладёт её во вложенный объект `page`. */
@Serializable
data class PageInfo(
    val size: Int,
    val number: Int,
    val totalElements: Long,
    val totalPages: Int,
)


@Serializable
data class PaymentDto(
    val id: String,
    val amount: Double,
    val currency: String,
    val status: PaymentStatus,
    val confirmationUrl: String? = null,
    val createdAt: String,
)


/**
 * @property amount сумма
 *
 * Body запроса на сервер `/api/billing/payments`
 */
@Serializable
data class CreatePaymentDto(val amount: String) // TODO то же самое