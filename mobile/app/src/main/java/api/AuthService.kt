package com.staffguard.mobile.api

import com.staffguard.mobile.models.LoginRequest
import com.staffguard.mobile.models.LoginResponse
import com.staffguard.mobile.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<LoginResponse>
}