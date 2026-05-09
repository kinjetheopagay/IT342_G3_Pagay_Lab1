package com.staffguard.mobile.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.models.AttendanceResponse
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter(private val records: List<AttendanceResponse>) :
    RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        // Format date
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(record.date)
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
            val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
            val weekdayFormat = SimpleDateFormat("EEE", Locale.getDefault())

            holder.tvMonth.text = monthFormat.format(date!!).uppercase()
            holder.tvDay.text = dayFormat.format(date)
            holder.tvWeekday.text = weekdayFormat.format(date).uppercase()
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDay.text = ""
            holder.tvWeekday.text = ""
        }

        holder.tvStatus.text = record.status
        holder.tvBadge.text = record.status

        val timeIn = record.timeIn?.take(5) ?: "N/A"
        val timeOut = record.timeOut?.take(5) ?: "N/A"
        holder.tvTime.text = "Check In: $timeIn · Check Out: $timeOut"

        // Badge color
        val color = when (record.status) {
            "PRESENT" -> Color.parseColor("#1D9E75")
            "ABSENT" -> Color.parseColor("#E24B4A")
            "REST_DAY" -> Color.parseColor("#9BA4C7")
            else -> Color.parseColor("#9BA4C7")
        }
        holder.tvBadge.setBackgroundColor(color)
    }

    override fun getItemCount() = records.size
}