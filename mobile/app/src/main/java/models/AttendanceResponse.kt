package com.staffguard.mobile.models

data class AttendanceResponse(
    val id: Long,
    val employeeName: String,
    val date: String,
    val timeIn: String?,
    val timeOut: String?,
    val status: String
)