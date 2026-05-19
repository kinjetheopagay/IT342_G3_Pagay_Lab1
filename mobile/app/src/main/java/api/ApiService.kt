package com.staffguard.mobile.api

import com.staffguard.mobile.models.AttendanceResponse
import com.staffguard.mobile.models.ScheduleRequest
import com.staffguard.mobile.models.CashRecordResponse
import com.staffguard.mobile.models.IncidentRequest
import com.staffguard.mobile.models.IncidentResponse
import com.staffguard.mobile.models.ScheduleResponse
import com.staffguard.mobile.models.ProfilePictureRequest
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

    @GET("incidents/all")
    fun getAllIncidents(@Header("Authorization") token: String): Call<List<IncidentResponse>>

    @PUT("incidents/{id}/status")
    fun updateIncidentStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Query("status") status: String
    ): Call<Any>

    @GET("attendance/all")
    fun getAllAttendance(@Header("Authorization") token: String): Call<List<AttendanceResponse>>

    @GET("user/all")
    fun getAllUsers(@Header("Authorization") token: String): Call<List<User>>

    @DELETE("user/{id}")
    fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Long): Call<Any>

    @GET("cash-records/all")
    fun getAllCashRecords(@Header("Authorization") token: String): Call<List<CashRecordResponse>>

    @GET("schedules/all")
    fun getAllSchedules(@Header("Authorization") token: String): Call<List<ScheduleResponse>>

    @DELETE("schedules/{id}")
    fun deleteSchedule(@Header("Authorization") token: String, @Path("id") id: Long): Call<Any>

    @POST("schedules")
    fun createSchedule(@Body request: ScheduleRequest): Call<Any>

    @PUT("user/profile-picture")
    fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Body request: ProfilePictureRequest
    ): Call<Any>
}