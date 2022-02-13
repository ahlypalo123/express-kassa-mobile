package com.hlypalo.express_kassa.data.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class LoginResponse (
    val token: String,
    val refresh_token: String
) : Serializable

data class ErrorBody (
    val error: String,
    val message: String?
)