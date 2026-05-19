package com.staffguard.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.utils.TokenManager

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<LinearLayout>(R.id.cardIncidents).setOnClickListener {
            startActivity(Intent(this, AdminIncidentsActivity::class.java))
        }

        // TODO: uncomment as we build each screen
         findViewById<LinearLayout>(R.id.cardAttendance).setOnClickListener {
             startActivity(Intent(this, AdminAttendanceActivity::class.java))
         }

         findViewById<LinearLayout>(R.id.cardSchedule).setOnClickListener {
             startActivity(Intent(this, AdminScheduleActivity::class.java))
    }

         findViewById<LinearLayout>(R.id.cardCashRecords).setOnClickListener {
             startActivity(Intent(this, AdminCashRecordsActivity::class.java))
         }

         findViewById<LinearLayout>(R.id.cardEmployees).setOnClickListener {
             startActivity(Intent(this, AdminEmployeesActivity::class.java))
         }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            TokenManager.clearAll(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}