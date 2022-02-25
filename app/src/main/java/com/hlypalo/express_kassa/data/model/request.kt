package com.hlypalo.express_kassa.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationRequest (
    val email: String? = null,
    val password: String? = null,
)

@JsonClass(generateAdapter = true)
data class ValidateRequest (
    val email: String?,
    val code: Int,
)

@JsonClass(generateAdapter = true)
data class ShiftRequest(
    val employee_name: String
)

enum class PaymentMethod {
    CASH, CARD
}