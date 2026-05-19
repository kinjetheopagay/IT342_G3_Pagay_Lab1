package com.staffguard.mobile.models

data class ScheduleResponse(
    val id: Long,
    val date: String,
    val shiftStart: String,
    val shiftEnd: String,
    val supervisorName: String,
    val employeeNames: List<String>
)