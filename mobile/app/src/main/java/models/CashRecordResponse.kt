package com.staffguard.mobile.models

data class CashRecordResponse(
    val id: Long,
    val date: String,
    val pos: String,
    val totalSales: Double,
    val amount: Double?,
    val status: String,
    val employeeName: String?,
    val supervisorName: String?,
    val timePosted: String?
)