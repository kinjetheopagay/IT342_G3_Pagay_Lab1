package com.staffguard.mobile.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.CashRecordResponse
import android.widget.TextView
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CashRecordsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_records)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val rvCashRecords = findViewById<RecyclerView>(R.id.rvCashRecords)

        btnBack.setOnClickListener { finish() }
        rvCashRecords.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getMyCashRecords(token).enqueue(object : Callback<List<CashRecordResponse>> {
            override fun onResponse(
                call: Call<List<CashRecordResponse>>,
                response: Response<List<CashRecordResponse>>
            ) {
                if (response.isSuccessful) {
                    val records = response.body() ?: emptyList()
                    rvCashRecords.adapter = CashRecordsAdapter(records)
                }
            }
            override fun onFailure(call: Call<List<CashRecordResponse>>, t: Throwable) {}
        })
    }
}