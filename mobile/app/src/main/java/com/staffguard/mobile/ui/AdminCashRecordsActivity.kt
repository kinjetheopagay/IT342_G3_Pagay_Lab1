package com.staffguard.mobile.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.CashRecordRequest
import com.staffguard.mobile.models.CashRecordResponse
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminCashRecordsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var token: String
    private lateinit var apiService: ApiService
    private var allUsers: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cash_records)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerAdminCashRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        token = TokenManager.getBearerToken(this)
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Load users for the form
        apiService.getAllUsers(token).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    allUsers = response.body()?.filter { it.role == "EMPLOYEE" } ?: emptyList()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {}
        })

        findViewById<Button>(R.id.btnAddCashRecord).setOnClickListener {
            showAddDialog()
        }

        loadCashRecords()
    }

    private fun loadCashRecords() {
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
                    Toast.makeText(this@AdminCashRecordsActivity, "Failed to load", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showAddDialog() {
        if (allUsers.isEmpty()) {
            Toast.makeText(this, "No employees loaded yet. Try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_cash_record, null)

        val spinnerEmployee = dialogView.findViewById<Spinner>(R.id.spinnerEmployee)
        val spinnerSupervisor = dialogView.findViewById<Spinner>(R.id.spinnerSupervisor)
        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val spinnerPos = dialogView.findViewById<Spinner>(R.id.spinnerPos)
        val etTotalSales = dialogView.findViewById<EditText>(R.id.etTotalSales)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)
        val tvAmountLabel = dialogView.findViewById<TextView>(R.id.tvAmountLabel)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val tvError = dialogView.findViewById<TextView>(R.id.tvError)

        // Setup employee spinner
        val userNames = allUsers.map { "${it.name} (${it.employeeId})" }
        val userAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEmployee.adapter = userAdapter
        spinnerSupervisor.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,
            allUsers.map { it.name }).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // POS spinner
        val posList = listOf("POS A", "POS B", "POS C")
        spinnerPos.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, posList).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Status spinner
        val statusList = listOf("FLAT", "SHORT", "OVER")
        spinnerStatus.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, statusList).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Show/hide amount field based on status
        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                if (statusList[pos] == "FLAT") {
                    tvAmountLabel.visibility = View.GONE
                    etAmount.visibility = View.GONE
                } else {
                    tvAmountLabel.visibility = View.VISIBLE
                    etAmount.visibility = View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Date picker
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        etDate.isFocusable = false

        // Set today as default date
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
        etDate.setText(today)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            tvError.visibility = View.GONE

            val date = etDate.text.toString().trim()
            val totalSalesStr = etTotalSales.text.toString().trim()

            if (date.isEmpty() || totalSalesStr.isEmpty()) {
                tvError.text = "Please fill in all required fields"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val employeeId = allUsers[spinnerEmployee.selectedItemPosition].id
            val supervisorId = allUsers[spinnerSupervisor.selectedItemPosition].id
            val pos = posList[spinnerPos.selectedItemPosition]
            val status = statusList[spinnerStatus.selectedItemPosition]
            val totalSales = totalSalesStr.toDoubleOrNull() ?: 0.0
            val amount = if (status != "FLAT") {
                etAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
            } else 0.0

            val request = CashRecordRequest(
                date = date,
                pos = pos,
                totalSales = totalSales,
                amount = amount,
                status = status,
                supervisorId = supervisorId
            )

            apiService.addCashRecord(token, employeeId, request)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AdminCashRecordsActivity,
                                "Cash record added!",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                            loadCashRecords()
                        } else {
                            tvError.text = "Failed to add cash record"
                            tvError.visibility = View.VISIBLE
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        tvError.text = "Connection error"
                        tvError.visibility = View.VISIBLE
                    }
                })
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

        val fmt = NumberFormat.getNumberInstance(Locale("en", "PH")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        holder.tvPos.text = record.pos
        holder.tvTotalSales.text = "Total Sales: ₱${fmt.format(record.totalSales)}"
        holder.tvEmployee.text = "Employee: ${record.employeeName ?: "N/A"}"
        holder.tvSupervisor.text = "Supervisor: ${record.supervisorName ?: "N/A"}"

        val statusColor = when (record.status) {
            "FLAT"  -> Color.parseColor("#1D9E75")
            "SHORT" -> Color.parseColor("#E24B4A")
            "OVER"  -> Color.parseColor("#E85D24")
            else    -> Color.parseColor("#9BA4C7")
        }
        holder.tvStatus.text = record.status
        holder.tvStatus.setBackgroundColor(statusColor)

        val amt = record.amount ?: 0.0
        holder.tvAmount.text = when (record.status) {
            "FLAT"  -> "₱ 0"
            "SHORT" -> "- ₱${fmt.format(kotlin.math.abs(amt))}"
            "OVER"  -> "+ ₱${fmt.format(kotlin.math.abs(amt))}"
            else    -> "₱${fmt.format(amt)}"
        }
    }

    override fun getItemCount() = records.size
}