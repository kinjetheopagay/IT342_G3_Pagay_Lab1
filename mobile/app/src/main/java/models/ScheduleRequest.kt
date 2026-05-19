package com.staffguard.mobile.models

import com.google.gson.annotations.SerializedName

data class ScheduleRequest(
    @SerializedName("supervisorId") val supervisorId: Long,
    @SerializedName("employeeIds") val employeeIds: List<Long>,
    @SerializedName("date") val date: String,
    @SerializedName("shiftStart") val shiftStart: String,
    @SerializedName("shiftEnd") val shiftEnd: String
)