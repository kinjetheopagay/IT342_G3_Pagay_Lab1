package com.staffguard.mobile.models

data class LoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val token: String
)