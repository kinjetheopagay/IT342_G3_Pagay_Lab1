package com.staffguard.mobile.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "EMPLOYEE"
)