package com.staffguard.mobile.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.IncidentResponse
import com.staffguard.mobile.utils.TokenManager
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminIncidentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_incidents)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminIncidents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        fun loadIncidents() {
            apiService.getAllIncidents(token)
                .enqueue(object : Callback<List<IncidentResponse>> {
                    override fun onResponse(
                        call: Call<List<IncidentResponse>>,
                        response: Response<List<IncidentResponse>>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body() ?: emptyList()
                            recyclerView.adapter = AdminIncidentsAdapter(list) { incident, status ->
                                apiService.updateIncidentStatus(token, incident.id, status)
                                    .enqueue(object : Callback<Any> {
                                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    this@AdminIncidentsActivity,
                                                    "$status successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loadIncidents()
                                            }
                                        }
                                        override fun onFailure(call: Call<Any>, t: Throwable) {
                                            Toast.makeText(
                                                this@AdminIncidentsActivity,
                                                "Action failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<IncidentResponse>>, t: Throwable) {
                        Toast.makeText(
                            this@AdminIncidentsActivity,
                            "Failed to load incidents",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        loadIncidents()
    }
}