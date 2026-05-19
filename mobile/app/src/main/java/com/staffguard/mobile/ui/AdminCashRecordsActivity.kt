package com.staffguard.mobile.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.CashRecordResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdminCashRecordsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cash_records)
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminCashRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getAllCashRecords(token)
            .enqueue(object : Callback<List<CashRecordResponse>> {
                override fun onResponse(
                    call: Call<List<CashRecordResponse>>,
                    response: Response<List<CashRecordResponse>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        recyclerView.adapter = AdminCashRecordsAdapter(list)
                    }
                }
                override fun onFailure(call: Call<List<CashRecordResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@AdminCashRecordsActivity,
                        "Failed to load cash records",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

class AdminCashRecordsAdapter(
    private val records: List<CashRecordResponse>
) : RecyclerView.Adapter<AdminCashRecordsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvPos: TextView = view.findViewById(R.id.tvPos)
        val tvTotalSales: TextView = view.findViewById(R.id.tvTotalSales)
        val tvEmployee: TextView = view.findViewById(R.id.tvEmployee)
        val tvSupervisor: TextView = view.findViewById(R.id.tvSupervisor)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_cash_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        // Date box
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(record.date)
            holder.tvMonth.text = SimpleDateFormat("MMM", Locale.getDefault()).format(date!!).uppercase()
            holder.tvDay.text = SimpleDateFormat("d", Locale.getDefault()).format(date)
            holder.tvWeekday.text = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDay.text = ""
            holder.tvWeekday.text = ""
        }

        // Format money — matches web ₱ format
        val fmt = NumberFormat.getNumberInstance(Locale("en", "PH")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        holder.tvPos.text = record.pos
        holder.tvTotalSales.text = "Total Sales: ₱${fmt.format(record.totalSales)}"
        holder.tvEmployee.text = "Employee: ${record.employeeName ?: "N/A"}"
        holder.tvSupervisor.text = "Supervisor: ${record.supervisorName ?: "N/A"}"

        // Status badge color — matches web exactly
        val statusColor = when (record.status) {
            "FLAT"  -> Color.parseColor("#1D9E75")
            "SHORT" -> Color.parseColor("#E24B4A")
            "OVER"  -> Color.parseColor("#E85D24")
            else    -> Color.parseColor("#9BA4C7")
        }
        holder.tvStatus.text = record.status
        holder.tvStatus.setBackgroundColor(statusColor)

        // Amount display — matches web formatAmount logic
        holder.tvAmount.text = when (record.status) {
            "FLAT"  -> "₱ 0"
            "SHORT" -> "- ₱${fmt.format(Math.abs(record.amount ?: 0.0))}"
            "OVER"  -> "+ ₱${fmt.format(Math.abs(record.amount ?: 0.0))}"
            else    -> "₱${fmt.format(record.amount ?: 0.0)}"
        }
    }

    override fun getItemCount() = records.size
}