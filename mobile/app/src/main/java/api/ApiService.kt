package com.staffguard.mobile.api

import com.staffguard.mobile.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("user/me")
    fun getMe(@Header("Authorization") token: String): Call<User>
}