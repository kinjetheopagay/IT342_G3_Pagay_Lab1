package com.staffguard.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.AttendanceResponse
import com.staffguard.mobile.models.ScheduleResponse
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvSchedule: TextView
    private lateinit var btnCheckIn: Button
    private lateinit var btnAttendance: android.widget.LinearLayout
    private lateinit var btnSubmitIncident: android.widget.LinearLayout
    private lateinit var btnMyIncidents: android.widget.LinearLayout
    private lateinit var btnCashRecords: android.widget.LinearLayout
    private lateinit var btnProfile: Button
    private lateinit var btnMySchedule: Button
    private lateinit var btnLogout: Button

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvRole = findViewById(R.id.tvRole)
        tvSchedule = findViewById(R.id.tvSchedule)
        btnCheckIn = findViewById(R.id.btnCheckIn)
        btnAttendance = findViewById(R.id.btnAttendance)
        btnSubmitIncident = findViewById(R.id.btnSubmitIncident)
        btnMyIncidents = findViewById(R.id.btnMyIncidents)
        btnCashRecords = findViewById(R.id.btnCashRecords)
        btnProfile = findViewById(R.id.btnProfile)
        btnMySchedule = findViewById(R.id.btnMySchedule)
        btnLogout = findViewById(R.id.btnLogout)

        loadUserInfo()
        loadTodayAttendance()

        btnCheckIn.setOnClickListener { handleCheckIn() }

        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        btnSubmitIncident.setOnClickListener {
            startActivity(Intent(this, SubmitIncidentActivity::class.java))
        }
        btnMyIncidents.setOnClickListener {
            startActivity(Intent(this, MyIncidentsActivity::class.java))
        }
        btnCashRecords.setOnClickListener {
            startActivity(Intent(this, CashRecordsActivity::class.java))
        }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        btnMySchedule.setOnClickListener {
            startActivity(Intent(this, MyScheduleActivity::class.java))
        }

        // Logout with confirmation
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("YES, LOGOUT") { _, _ ->
                    TokenManager.clearAll(this)
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                .setNegativeButton("CANCEL", null)
                .show()
        }
    }

    private fun loadUserInfo() {
        val token = TokenManager.getBearerToken(this)
        apiService.getMe(token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    tvWelcome.text = "Welcome, ${user?.name}!"
                    tvRole.text = "Role: ${user?.role}"
                } else {
                    TokenManager.clearAll(this@DashboardActivity)
                    startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                    finish()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Cannot connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadTodayAttendance() {
        val token = TokenManager.getBearerToken(this)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        apiService.getMyAttendance(token).enqueue(object : Callback<List<AttendanceResponse>> {
            override fun onResponse(
                call: Call<List<AttendanceResponse>>,
                response: Response<List<AttendanceResponse>>
            ) {
                if (response.isSuccessful) {
                    val todayRecord = response.body()?.find { it.date == today }
                    updateCheckInButton(todayRecord)
                }
            }
            override fun onFailure(call: Call<List<AttendanceResponse>>, t: Throwable) {}
        })
    }

    private fun updateCheckInButton(record: AttendanceResponse?) {
        when {
            record == null -> {
                btnCheckIn.text = "CHECK IN"
                btnCheckIn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFFE85D24.toInt())
                btnCheckIn.isEnabled = true
                tvSchedule.text = "No time in recorded yet"
            }
            record.timeOut == null -> {
                btnCheckIn.text = "CHECK OUT"
                btnCheckIn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFF1D9E75.toInt())
                btnCheckIn.isEnabled = true
                tvSchedule.text = "Time In: ${record.timeIn?.take(5)}"
            }
            else -> {
                btnCheckIn.text = "ATTENDANCE COMPLETE ✓"
                btnCheckIn.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(0xFF4A3DB5.toInt())
                btnCheckIn.isEnabled = false
                tvSchedule.text = "In: ${record.timeIn?.take(5)} · Out: ${record.timeOut?.take(5)}"
            }
        }
    }

    private fun handleCheckIn() {
        val token = TokenManager.getBearerToken(this)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // First check today's schedule
        apiService.getMyTodaySchedule(token).enqueue(object : Callback<ScheduleResponse?> {
            override fun onResponse(
                call: Call<ScheduleResponse?>,
                response: Response<ScheduleResponse?>
            ) {
                val schedule = response.body()

                if (schedule == null) {
                    // No schedule today — show warning
                    showShiftWarning(token, today)
                    return
                }

                // Check if current time is within shift hours
                val now = Calendar.getInstance()
                val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

                val shiftParts = schedule.shiftStart.split(":")
                val endParts = schedule.shiftEnd.split(":")
                val shiftStartMinutes = shiftParts[0].toInt() * 60 + shiftParts[1].toInt()
                val shiftEndMinutes = endParts[0].toInt() * 60 + endParts[1].toInt()

                if (currentMinutes < shiftStartMinutes || currentMinutes > shiftEndMinutes) {
                    // Outside shift hours — show warning
                    showShiftWarning(token, today)
                } else {
                    // Within shift — check in directly
                    doCheckIn(token, today)
                }
            }
            override fun onFailure(call: Call<ScheduleResponse?>, t: Throwable) {
                // Connection error — show warning
                showShiftWarning(token, today)
            }
        })
    }

    private fun showShiftWarning(token: String, today: String) {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Not Your Shift")
            .setMessage("You have no assigned shift today or you are outside your scheduled shift hours. Do you still want to check in?")
            .setPositiveButton("STILL CHECK IN") { _, _ ->
                doCheckIn(token, today)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun doCheckIn(token: String, today: String) {
        apiService.getMyAttendance(token).enqueue(object : Callback<List<AttendanceResponse>> {
            override fun onResponse(
                call: Call<List<AttendanceResponse>>,
                response: Response<List<AttendanceResponse>>
            ) {
                val todayRecord = response.body()?.find { it.date == today }
                if (todayRecord == null || todayRecord.timeIn == null) {
                    apiService.timeIn(token).enqueue(object : Callback<AttendanceResponse> {
                        override fun onResponse(call: Call<AttendanceResponse>, response: Response<AttendanceResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@DashboardActivity, "✅ Time In recorded!", Toast.LENGTH_SHORT).show()
                                loadTodayAttendance()
                            } else {
                                Toast.makeText(this@DashboardActivity, "Already timed in today", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                            Toast.makeText(this@DashboardActivity, "Connection error", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    apiService.timeOut(token).enqueue(object : Callback<AttendanceResponse> {
                        override fun onResponse(call: Call<AttendanceResponse>, response: Response<AttendanceResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@DashboardActivity, "✅ Time Out recorded!", Toast.LENGTH_SHORT).show()
                                loadTodayAttendance()
                            } else {
                                Toast.makeText(this@DashboardActivity, "Already timed out today", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<AttendanceResponse>, t: Throwable) {
                            Toast.makeText(this@DashboardActivity, "Connection error", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            override fun onFailure(call: Call<List<AttendanceResponse>>, t: Throwable) {}
        })
    }

    override fun onResume() {
        super.onResume()
        loadTodayAttendance()
    }
}