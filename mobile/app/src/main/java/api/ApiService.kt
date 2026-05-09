package com.staffguard.mobile.api

import com.staffguard.mobile.models.AttendanceResponse
import com.staffguard.mobile.models.CashRecordResponse
import com.staffguard.mobile.models.IncidentRequest
import com.staffguard.mobile.models.IncidentResponse
import com.staffguard.mobile.models.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("user/me")
    fun getMe(@Header("Authorization") token: String): Call<User>

    @POST("attendance/time-in")
    fun timeIn(@Header("Authorization") token: String): Call<AttendanceResponse>

    @POST("attendance/time-out")
    fun timeOut(@Header("Authorization") token: String): Call<AttendanceResponse>

    @GET("attendance/my")
    fun getMyAttendance(@Header("Authorization") token: String): Call<List<AttendanceResponse>>

    @POST("incidents")
    fun submitIncident(
        @Header("Authorization") token: String,
        @Body request: IncidentRequest
    ): Call<IncidentResponse>

    @GET("incidents/my")
    fun getMyIncidents(@Header("Authorization") token: String): Call<List<IncidentResponse>>

    @GET("cash-records/my")
    fun getMyCashRecords(@Header("Authorization") token: String): Call<List<CashRecordResponse>>
}