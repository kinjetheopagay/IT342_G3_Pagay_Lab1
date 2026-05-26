package com.staffguard.mobile.ui

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
import com.staffguard.mobile.models.ScheduleRequest
import com.staffguard.mobile.models.ScheduleResponse
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class AdminScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var token: String
    private lateinit var apiService: ApiService
    private var allUsers: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_schedule)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerSchedule)
        recyclerView.layoutManager = LinearLayoutManager(this)

        token = TokenManager.getBearerToken(this)
        apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Load users for the create form
        apiService.getAllUsers(token).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    allUsers = response.body()?.filter { it.role == "EMPLOYEE" } ?: emptyList()
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {}
        })

        findViewById<Button>(R.id.btnCreateSchedule).setOnClickListener {
            showCreateDialog()
        }

        loadSchedules()
    }

    private fun loadSchedules() {
        apiService.getAllSchedules(token)
            .enqueue(object : Callback<List<ScheduleResponse>> {
                override fun onResponse(
                    call: Call<List<ScheduleResponse>>,
                    response: Response<List<ScheduleResponse>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        recyclerView.adapter = AdminScheduleAdapter(list) { schedule ->
                            AlertDialog.Builder(this@AdminScheduleActivity)
                                .setTitle("Delete Schedule?")
                                .setMessage("Delete this schedule? This cannot be undone.")
                                .setPositiveButton("YES, DELETE") { _, _ ->
                                    apiService.deleteSchedule(token, schedule.id)
                                        .enqueue(object : Callback<Any> {
                                            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(this@AdminScheduleActivity, "Schedule deleted", Toast.LENGTH_SHORT).show()
                                                    loadSchedules()
                                                }
                                            }
                                            override fun onFailure(call: Call<Any>, t: Throwable) {
                                                Toast.makeText(this@AdminScheduleActivity, "Delete failed", Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                }
                                .setNegativeButton("CANCEL", null)
                                .show()
                        }
                    }
                }
                override fun onFailure(call: Call<List<ScheduleResponse>>, t: Throwable) {
                    Toast.makeText(this@AdminScheduleActivity, "Failed to load schedules", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showCreateDialog() {
        if (allUsers.isEmpty()) {
            Toast.makeText(this, "No employees loaded yet. Try again.", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_schedule, null)

        val etDate = dialogView.findViewById<EditText>(R.id.etDate)
        val etShiftStart = dialogView.findViewById<EditText>(R.id.etShiftStart)
        val etShiftEnd = dialogView.findViewById<EditText>(R.id.etShiftEnd)

        // Date picker on tap
        etDate.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDate.setText(
                        String.format("%04d-%02d-%02d", year, month + 1, day)
                    )
                },
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH),
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }
        etDate.isFocusable = false

        // Shift start time picker on tap
        etShiftStart.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    etShiftStart.setText(String.format("%02d:%02d", hour, minute))
                },
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }
        etShiftStart.isFocusable = false

        // Shift end time picker on tap
        etShiftEnd.setOnClickListener {
            val cal = java.util.Calendar.getInstance()
            android.app.TimePickerDialog(
                this,
                { _, hour, minute ->
                    etShiftEnd.setText(String.format("%02d:%02d", hour, minute))
                },
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }
        etShiftEnd.isFocusable = false


        val spinnerSupervisor = dialogView.findViewById<Spinner>(R.id.spinnerSupervisor)
        val layoutEmployeeSpinners = dialogView.findViewById<LinearLayout>(R.id.layoutEmployeeSpinners)
        val btnAddEmployee = dialogView.findViewById<Button>(R.id.btnAddEmployee)
        val tvError = dialogView.findViewById<TextView>(R.id.tvError)

        // Setup supervisor spinner
        val userNames = allUsers.map { "${it.name} (${it.employeeId})" }
        val supervisorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        supervisorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSupervisor.adapter = supervisorAdapter

        // Start with 3 employee spinners — minimum required
        val employeeSpinners = mutableListOf<Spinner>()

        fun addEmployeeSpinner() {
            val spinner = Spinner(this).apply {
                adapter = ArrayAdapter(
                    this@AdminScheduleActivity,
                    android.R.layout.simple_spinner_item,
                    userNames
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
            }
            employeeSpinners.add(spinner)
            layoutEmployeeSpinners.addView(spinner)
        }

        // Add 3 by default
        repeat(3) { addEmployeeSpinner() }

        btnAddEmployee.setOnClickListener { addEmployeeSpinner() }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            tvError.visibility = View.GONE

            val date = etDate.text.toString().trim()
            val shiftStart = etShiftStart.text.toString().trim()
            val shiftEnd = etShiftEnd.text.toString().trim()

            if (date.isEmpty() || shiftStart.isEmpty() || shiftEnd.isEmpty()) {
                tvError.text = "Please fill in all fields"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val supervisorId = allUsers[spinnerSupervisor.selectedItemPosition].id
            val employeeIds = employeeSpinners.map { allUsers[it.selectedItemPosition].id }

            // Check duplicates
            if (employeeIds.toSet().size != employeeIds.size) {
                tvError.text = "Duplicate employees selected"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Check supervisor not in employee list
            if (employeeIds.contains(supervisorId)) {
                tvError.text = "Supervisor cannot also be an employee"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val request = ScheduleRequest(
                supervisorId = supervisorId,
                employeeIds = employeeIds,
                date = date,
                shiftStart = "$shiftStart:00",
                shiftEnd = "$shiftEnd:00"
            )

            apiService.createSchedule(request)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminScheduleActivity, "Schedule created!", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                            loadSchedules()
                        } else {
                            tvError.text = "Failed to create schedule"
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

class AdminScheduleAdapter(
    private val schedules: List<ScheduleResponse>,
    private val onDelete: (ScheduleResponse) -> Unit
) : RecyclerView.Adapter<AdminScheduleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShift: TextView = view.findViewById(R.id.tvShift)
        val tvSupervisor: TextView = view.findViewById(R.id.tvSupervisor)
        val layoutEmployees: LinearLayout = view.findViewById(R.id.layoutEmployees)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(schedule.date)
            val displayFmt = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            holder.tvDate.text = displayFmt.format(date!!)
        } catch (e: Exception) {
            holder.tvDate.text = schedule.date
        }

        holder.tvShift.text = "🕐 ${schedule.shiftStart} — ${schedule.shiftEnd}"
        holder.tvSupervisor.text = "👑 ${schedule.supervisorName}"

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

        holder.btnDelete.setOnClickListener { onDelete(schedule) }
    }

    override fun getItemCount() = schedules.size
}