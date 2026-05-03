package com.staffguard.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.AuthService
import com.staffguard.mobile.models.LoginResponse
import com.staffguard.mobile.models.RegisterRequest
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.name)
        val emailInput = findViewById<EditText>(R.id.email)
        val passwordInput = findViewById<EditText>(R.id.password)
        val registerButton = findViewById<Button>(R.id.registerBtn)
        val loginLink = findViewById<TextView>(R.id.tvLogin)

        val authService = ApiClient.retrofit.create(AuthService::class.java)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(name, email, password)

            authService.register(request).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            // ✅ Save token and go to Dashboard
                            TokenManager.saveToken(this@RegisterActivity, token)
                            Toast.makeText(this@RegisterActivity, "Registered successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Cannot connect to server", Toast.LENGTH_SHORT).show()
                }
            })
        }

        loginLink.setOnClickListener {
            finish() // Go back to Login
        }
    }
}