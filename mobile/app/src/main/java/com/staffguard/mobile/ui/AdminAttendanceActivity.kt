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
import com.staffguard.mobile.models.AttendanceResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class AdminAttendanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_attendance)
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerAdminAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getAllAttendance(token)
            .enqueue(object : Callback<List<AttendanceResponse>> {
                override fun onResponse(
                    call: Call<List<AttendanceResponse>>,
                    response: Response<List<AttendanceResponse>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        recyclerView.adapter = AdminAttendanceAdapter(list)
                    }
                }
                override fun onFailure(call: Call<List<AttendanceResponse>>, t: Throwable) {
                    Toast.makeText(
                        this@AdminAttendanceActivity,
                        "Failed to load attendance",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}

class AdminAttendanceAdapter(
    private val records: List<AttendanceResponse>
) : RecyclerView.Adapter<AdminAttendanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvEmployeeName: TextView = view.findViewById(R.id.tvEmployeeName)
        val tvTimes: TextView = view.findViewById(R.id.tvTimes)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

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

        holder.tvEmployeeName.text = record.employeeName ?: "Unknown"
        holder.tvTimes.text = "In: ${record.timeIn?.take(5) ?: "N/A"}  ·  Out: ${record.timeOut?.take(5) ?: "N/A"}"

        holder.tvStatus.text = record.status
        val color = when (record.status) {
            "PRESENT" -> Color.parseColor("#1D9E75")
            "ABSENT"  -> Color.parseColor("#E24B4A")
            else      -> Color.parseColor("#9BA4C7")
        }
        holder.tvStatus.setBackgroundColor(color)
    }

    override fun getItemCount() = records.size
}