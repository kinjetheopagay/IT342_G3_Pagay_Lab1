package com.staffguard.mobile.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.AuthService
import com.staffguard.mobile.models.LoginRequest
import com.staffguard.mobile.models.LoginResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Already logged in
        if (TokenManager.isLoggedIn(this)) {
            goToDashboard()
            return
        }

        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginBtn)
        val registerLink = findViewById<TextView>(R.id.tvRegister)

        val authService = ApiClient.retrofit.create(AuthService::class.java)

        loginButton.setOnClickListener {

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authService.login(LoginRequest(email, password))
                .enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {

                        if (response.isSuccessful) {

                            val loginResponse = response.body()

                            loginResponse?.let {

                                // Save JWT token
                                TokenManager.saveToken(this@LoginActivity, it.token)

                                // Save role
                                val prefs = getSharedPreferences(
                                    "StaffGuardPrefs",
                                    Context.MODE_PRIVATE
                                )

                                prefs.edit()
                                    .putString("user_role", it.role)
                                    .apply()

                                Toast.makeText(
                                    this@LoginActivity,
                                    "Login successful!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Navigate based on role
                                if (it.role == "ADMIN") {

                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            AdminDashboardActivity::class.java
                                        )
                                    )

                                } else {

                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            DashboardActivity::class.java
                                        )
                                    )
                                }

                                finish()
                            }

                        } else {

                            Toast.makeText(
                                this@LoginActivity,
                                "Invalid email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Cannot connect to server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToDashboard() {

        val prefs = getSharedPreferences(
            "StaffGuardPrefs",
            Context.MODE_PRIVATE
        )

        val role = prefs.getString("user_role", "USER")

        if (role == "ADMIN") {

            startActivity(
                Intent(
                    this,
                    AdminDashboardActivity::class.java
                )
            )

        } else {

            startActivity(
                Intent(
                    this,
                    DashboardActivity::class.java
                )
            )
        }

        finish()
    }
}