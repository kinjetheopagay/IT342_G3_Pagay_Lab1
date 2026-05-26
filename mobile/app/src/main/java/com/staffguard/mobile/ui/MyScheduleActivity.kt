package com.staffguard.mobile.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.ScheduleResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyScheduleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerMySchedule)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        apiService.getMySchedules(token).enqueue(object : Callback<List<ScheduleResponse>> {
            override fun onResponse(
                call: Call<List<ScheduleResponse>>,
                response: Response<List<ScheduleResponse>>
            ) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    recyclerView.adapter = MyScheduleAdapter(list)
                }
            }
            override fun onFailure(call: Call<List<ScheduleResponse>>, t: Throwable) {
                Toast.makeText(this@MyScheduleActivity, "Failed to load schedules", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class MyScheduleAdapter(
    private val schedules: List<ScheduleResponse>
) : RecyclerView.Adapter<MyScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvToday: TextView = view.findViewById(R.id.tvToday)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShift: TextView = view.findViewById(R.id.tvShift)
        val tvSupervisor: TextView = view.findViewById(R.id.tvSupervisor)
        val layoutEmployees: LinearLayout = view.findViewById(R.id.layoutEmployees)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Show TODAY badge
        if (schedule.date == today) {
            holder.tvToday.visibility = View.VISIBLE
        } else {
            holder.tvToday.visibility = View.GONE
        }

        // Format date
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(schedule.date)
            val displayFmt = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            holder.tvDate.text = displayFmt.format(date!!)
        } catch (e: Exception) {
            holder.tvDate.text = schedule.date
        }

        holder.tvShift.text = "${schedule.shiftStart} — ${schedule.shiftEnd}"
        holder.tvSupervisor.text = "${schedule.supervisorName}"

        // Employee badges
        holder.layoutEmployees.removeAllViews()
        schedule.employeeNames.forEach { name ->
            val badge = TextView(holder.itemView.context).apply {
                text = "👤 $name"
                textSize = 12f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#4A3DB5"))
                setPadding(24, 8, 24, 8)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 8, 8) }
            }
            holder.layoutEmployees.addView(badge)
        }
    }

    override fun getItemCount() = schedules.size
}