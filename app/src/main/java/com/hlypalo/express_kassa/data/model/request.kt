package com.hlypalo.express_kassa.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationRequest (
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class ShiftRequest(
    val employee_name: String
)

@JsonClass(generateAdapter = true)
data class SaveCheckRequest (
    val discount: Float?,
    val customerName: String?,
    val customerLast4: Int?,
    val paymentMethod: PaymentMethod,
)

enum class PaymentMethod {
    CASH, CARD
}