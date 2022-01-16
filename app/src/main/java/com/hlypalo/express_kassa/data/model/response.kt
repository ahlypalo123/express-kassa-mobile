package com.hlypalo.express_kassa.data.model

data class LoginResponse (
    val token: String,
    val refresh_token: String
)