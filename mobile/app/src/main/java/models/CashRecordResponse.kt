package com.staffguard.mobile.models

data class CashRecordResponse(
    val id: Long,
    val employeeName: String,
    val supervisorName: String?,
    val date: String,
    val timePosted: String?,
    val pos: String,
    val totalSales: Double,
    val amount: Double,
    val status: String
)