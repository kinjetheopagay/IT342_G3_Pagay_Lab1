package com.staffguard.mobile.models

import com.google.gson.annotations.SerializedName

data class CashRecordRequest(
    @SerializedName("date") val date: String,
    @SerializedName("pos") val pos: String,
    @SerializedName("totalSales") val totalSales: Double,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("supervisorId") val supervisorId: Long
)