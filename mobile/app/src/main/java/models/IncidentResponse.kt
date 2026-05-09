package com.staffguard.mobile.models

data class IncidentResponse(
    val id: Long,
    val employeeName: String,
    val title: String,
    val description: String?,
    val supervisor: String,
    val date: String,
    val time: String?,
    val imageUrl: String?,
    val status: String
)