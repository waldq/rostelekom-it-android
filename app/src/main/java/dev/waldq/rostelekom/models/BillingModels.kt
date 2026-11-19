package dev.waldq.rostelekom.models

import dev.waldq.rostelekom.models.serialization.Money
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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
    val id: Uuid,
    val number: String,
    val balance: Money,
    val currency: String,
    val status: AccountStatus
)


@Serializable
data class TransactionDto(
    val id: Uuid,
    val type: TransactionType,
    val amount: Money,
    val balanceAfter: Money,
    val description: String? = null,
    val paymentId: Uuid? = null,
    val createdAt: String              // Instant → ISO-8601 строка
)

enum class PaymentStatus {
    PENDING,         // создан, ждём оплату
    SUCCEEDED,       // деньги зачислены
    CANCELED,        // отменён или не прошёл
    REFUND_REQUIRED, // оплата пришла, когда счёт уже закрыт
}

enum class TransactionType {
    TOP_UP, // пополнение (+)
    CHARGE, // списание, например абонентская плата (-)
    REFUND, // возврат (+)
}

@Serializable
data class PaymentDto(
    val id: Uuid,
    val amount: Money,
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
data class CreatePaymentDto(val amount: Money)