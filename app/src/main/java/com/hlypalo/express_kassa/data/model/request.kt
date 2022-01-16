package com.hlypalo.express_kassa.data.model

data class AuthenticationDto (
    val email: String,
    val password: String
)

data class SaveCheckRequest (
    val discount: Float?,
    val customerName: String?,
    val customerLast4: Int?,
    val paymentMethod: PaymentMethod,
)

enum class PaymentMethod {
    CASH, CARD
}