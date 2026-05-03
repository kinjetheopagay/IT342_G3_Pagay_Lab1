package com.staffguard.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvRole = findViewById<TextView>(R.id.tvRole)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnAttendance = findViewById<Button>(R.id.btnAttendance)
        val btnSubmitIncident = findViewById<Button>(R.id.btnSubmitIncident)
        val btnMyIncidents = findViewById<Button>(R.id.btnMyIncidents)
        val btnCashRecords = findViewById<Button>(R.id.btnCashRecords)

        // ✅ Load user info from API
        val apiService = ApiClient.retrofit.create(ApiService::class.java)
        val token = TokenManager.getBearerToken(this)

        apiService.getMe(token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    tvWelcome.text = "Welcome, ${user?.name}!"
                    tvRole.text = "Role: ${user?.role}"
                } else {
                    // Token invalid or expired
                    Toast.makeText(this@DashboardActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                    logout()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Cannot connect to server", Toast.LENGTH_SHORT).show()
            }
        })

        // ✅ Logout
        btnLogout.setOnClickListener {
            logout()
        }

        // ✅ Feature buttons (screens to be built next)
        btnAttendance.setOnClickListener {
            Toast.makeText(this, "Attendance - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnSubmitIncident.setOnClickListener {
            Toast.makeText(this, "Submit Incident - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnMyIncidents.setOnClickListener {
            Toast.makeText(this, "My Incidents - Coming Soon", Toast.LENGTH_SHORT).show()
        }

        btnCashRecords.setOnClickListener {
            Toast.makeText(this, "Cash Records - Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        TokenManager.clearToken(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}