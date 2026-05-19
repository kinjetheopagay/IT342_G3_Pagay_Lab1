package com.staffguard.mobile.models

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    val employeeId: String?,
    val profilePicture: String?
)