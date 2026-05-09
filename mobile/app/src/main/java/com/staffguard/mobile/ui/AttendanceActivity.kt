package com.staffguard.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.AttendanceResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val rvAttendance = findViewById<RecyclerView>(R.id.rvAttendance)

        btnBack.setOnClickListener { finish() }

        rvAttendance.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getMyAttendance(token).enqueue(object : Callback<List<AttendanceResponse>> {
            override fun onResponse(
                call: Call<List<AttendanceResponse>>,
                response: Response<List<AttendanceResponse>>
            ) {
                if (response.isSuccessful) {
                    val records = response.body() ?: emptyList()
                    rvAttendance.adapter = AttendanceAdapter(records)
                }
            }
            override fun onFailure(call: Call<List<AttendanceResponse>>, t: Throwable) {}
        })
    }
}