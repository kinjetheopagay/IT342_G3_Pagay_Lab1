package com.staffguard.mobile.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.models.IncidentResponse
import java.text.SimpleDateFormat
import java.util.Locale

class AdminIncidentsAdapter(
    private val incidents: List<IncidentResponse>,
    private val onAction: (IncidentResponse, String) -> Unit
) : RecyclerView.Adapter<AdminIncidentsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvEmployee: TextView = view.findViewById(R.id.tvEmployee)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val layoutButtons: LinearLayout = view.findViewById(R.id.layoutButtons)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_incident, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incident = incidents[position]

        // Parse date
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(incident.date)
            holder.tvMonth.text = SimpleDateFormat("MMM", Locale.getDefault()).format(date!!).uppercase()
            holder.tvDay.text = SimpleDateFormat("d", Locale.getDefault()).format(date)
            holder.tvWeekday.text = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDay.text = ""
            holder.tvWeekday.text = ""
        }

        holder.tvTitle.text = incident.title
        holder.tvEmployee.text = "By: ${incident.employeeName}"
        holder.tvDateTime.text = "Filed: ${incident.date} | ${incident.time}"

        if (incident.status == "PENDING") {
            holder.layoutButtons.visibility = View.VISIBLE
            holder.tvStatus.visibility = View.GONE
            holder.btnApprove.setOnClickListener { onAction(incident, "APPROVED") }
            holder.btnReject.setOnClickListener { onAction(incident, "REJECTED") }
        } else {
            holder.layoutButtons.visibility = View.GONE
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = incident.status
            val color = when (incident.status) {
                "APPROVED" -> Color.parseColor("#1D9E75")
                "REJECTED" -> Color.parseColor("#E24B4A")
                else -> Color.parseColor("#9BA4C7")
            }
            holder.tvStatus.setBackgroundColor(color)
        }

        // Whole card is clickable — shows detail dialog
        holder.itemView.setOnClickListener {
            showDetailDialog(holder.itemView, incident)
        }
    }

    private fun showDetailDialog(view: View, incident: IncidentResponse) {
        val context = view.context

        // Build dialog layout programmatically — matches web modal
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#2D3E6B"))
        }

        // Status badge
        val tvStatus = TextView(context).apply {
            text = incident.status
            textSize = 12f
            setTextColor(Color.WHITE)
            setPadding(24, 8, 24, 8)
            val color = when (incident.status) {
                "APPROVED" -> Color.parseColor("#1D9E75")
                "REJECTED" -> Color.parseColor("#E24B4A")
                else -> Color.parseColor("#E85D24")
            }
            setBackgroundColor(color)
        }
        layout.addView(tvStatus)

        // Helper to add label + value pairs
        fun addField(label: String, value: String?) {
            val tvLabel = TextView(context).apply {
                text = label
                textSize = 11f
                setTextColor(Color.parseColor("#9BA4C7"))
                setPadding(0, 24, 0, 4)
            }
            val tvValue = TextView(context).apply {
                text = value ?: "N/A"
                textSize = 14f
                setTextColor(Color.WHITE)
            }
            layout.addView(tvLabel)
            layout.addView(tvValue)
        }

        addField("SUBMITTED BY", incident.employeeName)
        addField("TITLE", incident.title)
        addField("DESCRIPTION", incident.description)
        addField("SUPERVISOR", incident.supervisor)
        addField("DATE & TIME", "${incident.date} at ${incident.time}")

        // Approve/Reject buttons if PENDING
        if (incident.status == "PENDING") {
            val btnRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 32, 0, 0)
            }

            val btnApprove = Button(context).apply {
                text = "APPROVE"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#1D9E75"))
                layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginEnd = 16
                }
            }

            val btnReject = Button(context).apply {
                text = "REJECT"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#E24B4A"))
                layoutParams = LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            btnRow.addView(btnApprove)
            btnRow.addView(btnReject)
            layout.addView(btnRow)

            val dialog = AlertDialog.Builder(context)
                .setView(layout)
                .create()
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnApprove.setOnClickListener {
                onAction(incident, "APPROVED")
                dialog.dismiss()
            }
            btnReject.setOnClickListener {
                onAction(incident, "REJECTED")
                dialog.dismiss()
            }

            dialog.show()
        } else {
            AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton("CLOSE", null)
                .create().also {
                    it.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    it.show()
                }
        }
    }

    override fun getItemCount() = incidents.size
}