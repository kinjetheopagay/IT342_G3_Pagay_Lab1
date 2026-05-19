package com.staffguard.mobile.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.models.CashRecordResponse
import java.text.SimpleDateFormat
import java.util.*

class CashRecordsAdapter(private val records: List<CashRecordResponse>) :
    RecyclerView.Adapter<CashRecordsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val tvWeekday: TextView = view.findViewById(R.id.tvWeekday)
        val tvPos: TextView = view.findViewById(R.id.tvPos)
        val tvTotalSales: TextView = view.findViewById(R.id.tvTotalSales)
        val tvSupervisor: TextView = view.findViewById(R.id.tvSupervisor)
        val tvTimePosted: TextView = view.findViewById(R.id.tvTimePosted)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cash_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]

        // Format date
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(record.date)
            holder.tvMonth.text = SimpleDateFormat("MMM", Locale.getDefault()).format(date!!).uppercase()
            holder.tvDay.text = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            holder.tvWeekday.text = SimpleDateFormat("EEE", Locale.getDefault()).format(date).uppercase()
        } catch (e: Exception) {
            holder.tvMonth.text = ""
            holder.tvDay.text = ""
            holder.tvWeekday.text = ""
        }

        holder.tvPos.text = record.pos
        holder.tvTotalSales.text = "Total Sales: ₱ ${"%,.2f".format(record.totalSales)}"
        holder.tvSupervisor.text = "Supervisor: ${record.supervisorName ?: "N/A"}"
        holder.tvTimePosted.text = "Time: ${record.timePosted ?: "--"}"
        holder.tvStatus.text = record.status

        // Status color
        val color = when (record.status) {
            "FLAT" -> Color.parseColor("#1D9E75")
            "SHORT" -> Color.parseColor("#E24B4A")
            "OVER" -> Color.parseColor("#E85D24")
            else -> Color.parseColor("#9BA4C7")
        }
        holder.tvStatus.setBackgroundColor(color)

        // Amount
        val amt = record.amount ?: 0.0
        holder.tvAmount.text = when (record.status) {
            "FLAT"  -> "₱ 0"
            "SHORT" -> "- ₱ ${"%,.2f".format(kotlin.math.abs(amt))}"
            "OVER"  -> "+ ₱ ${"%,.2f".format(kotlin.math.abs(amt))}"
            else    -> "₱ ${"%,.2f".format(amt)}"
        }
    }

    override fun getItemCount() = records.size
}