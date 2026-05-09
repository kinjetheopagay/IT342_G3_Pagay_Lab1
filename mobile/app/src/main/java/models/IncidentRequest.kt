package com.staffguard.mobile.models

data class IncidentRequest(
    val title: String,
    val description: String,
    val supervisor: String,
    val date: String,
    val time: String,
    val imageUrl: String?
)