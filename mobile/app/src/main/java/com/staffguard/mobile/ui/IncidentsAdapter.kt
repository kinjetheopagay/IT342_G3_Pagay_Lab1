package com.staffguard.mobile.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.models.IncidentResponse
import java.text.SimpleDateFormat
import java.util.*

class IncidentsAdapter(
    private val incidents: List<IncidentResponse>,
    private val onClick: (IncidentResponse) -> Unit
) : RecyclerView.Adapter<IncidentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvFiled: TextView = view.findViewById(R.id.tvFiled)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incident, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incident = incidents[position]

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(incident.date)
            holder.tvMonth.text = SimpleDateFormat("MMM", Locale.getDefault()).format(date!!).uppercase()
            holder.tvDay.text = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            holder.tvWeekday.text = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDay.text = ""
            holder.tvWeekday.text = ""
        }

        holder.tvTitle.text = incident.title
        holder.tvFiled.text = "Filed: ${incident.date} | ${incident.time?.take(5) ?: "--"}"
        holder.tvStatus.text = incident.status

        val color = when (incident.status) {
            "APPROVED" -> Color.parseColor("#1D9E75")
            "REJECTED" -> Color.parseColor("#E24B4A")
            else -> Color.parseColor("#E85D24")
        }
        holder.tvStatus.setBackgroundColor(color)

        holder.itemView.setOnClickListener { onClick(incident) }
    }

    override fun getItemCount() = incidents.size
}