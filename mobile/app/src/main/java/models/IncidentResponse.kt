package com.staffguard.mobile.models

data class IncidentResponse(
    val id: Long,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val status: String,
    val employeeName: String,
    val supervisor: String?,
    val imageUrl: String?
)