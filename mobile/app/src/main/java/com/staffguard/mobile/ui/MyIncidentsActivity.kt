package com.staffguard.mobile.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.IncidentResponse
import android.widget.TextView
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyIncidentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_incidents)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val rvIncidents = findViewById<RecyclerView>(R.id.rvIncidents)

        btnBack.setOnClickListener { finish() }
        rvIncidents.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getMyIncidents(token).enqueue(object : Callback<List<IncidentResponse>> {
            override fun onResponse(
                call: Call<List<IncidentResponse>>,
                response: Response<List<IncidentResponse>>
            ) {
                if (response.isSuccessful) {
                    val incidents = response.body() ?: emptyList()
                    rvIncidents.adapter = IncidentsAdapter(incidents) { incident ->
                        // Show detail dialog on click
                        IncidentDetailDialog(this@MyIncidentsActivity, incident).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<IncidentResponse>>, t: Throwable) {}
        })
    }
}