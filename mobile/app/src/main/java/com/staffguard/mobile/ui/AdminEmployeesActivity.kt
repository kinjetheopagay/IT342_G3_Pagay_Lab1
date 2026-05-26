package com.staffguard.mobile.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminEmployeesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_employees)
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerEmployees)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        fun loadEmployees() {
            apiService.getAllUsers(token)
                .enqueue(object : Callback<List<User>> {
                    override fun onResponse(
                        call: Call<List<User>>,
                        response: Response<List<User>>
                    ) {
                        if (response.isSuccessful) {
                            val list = response.body() ?: emptyList()
                            recyclerView.adapter = AdminEmployeesAdapter(list) { user ->
                                AlertDialog.Builder(this@AdminEmployeesActivity)
                                    .setTitle("Delete User?")
                                    .setMessage("Are you sure you want to delete ${user.name}? This cannot be undone.")
                                    .setPositiveButton("YES, DELETE") { _, _ ->
                                        apiService.deleteUser(token, user.id)
                                            .enqueue(object : Callback<Any> {
                                                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                                    if (response.isSuccessful) {
                                                        Toast.makeText(
                                                            this@AdminEmployeesActivity,
                                                            "User deleted",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        loadEmployees()
                                                    }
                                                }
                                                override fun onFailure(call: Call<Any>, t: Throwable) {
                                                    Toast.makeText(
                                                        this@AdminEmployeesActivity,
                                                        "Delete failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            })
                                    }
                                    .setNegativeButton("CANCEL", null)
                                    .show()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Toast.makeText(
                            this@AdminEmployeesActivity,
                            "Failed to load employees",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        loadEmployees()
    }
}

class AdminEmployeesAdapter(
    private val users: List<User>,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<AdminEmployeesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val ivAvatar: android.widget.ImageView = view.findViewById(R.id.ivAvatar)
        val tvAvatarPlaceholder: TextView = view.findViewById(R.id.tvAvatarPlaceholder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_employee, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvRole.text = user.role

        // Show profile picture if exists
        if (!user.profilePicture.isNullOrEmpty()) {
            try {
                val pureBase64 = if (user.profilePicture.contains(","))
                    user.profilePicture.split(",")[1]
                else
                    user.profilePicture
                val bytes = android.util.Base64.decode(pureBase64, android.util.Base64.DEFAULT)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.ivAvatar.setImageBitmap(bitmap)
                holder.ivAvatar.visibility = View.VISIBLE
                holder.tvAvatarPlaceholder.visibility = View.GONE
            } catch (e: Exception) {
                holder.ivAvatar.visibility = View.GONE
                holder.tvAvatarPlaceholder.visibility = View.VISIBLE
            }
        } else {
            holder.ivAvatar.visibility = View.GONE
            holder.tvAvatarPlaceholder.visibility = View.VISIBLE
        }

        val roleColor = if (user.role == "ADMIN")
            Color.parseColor("#E85D24")
        else
            Color.parseColor("#4A3DB5")
        holder.tvRole.setBackgroundColor(roleColor)

        // Hide delete for ADMIN accounts — same as web version
        if (user.role == "ADMIN") {
            holder.btnDelete.visibility = View.GONE
        } else {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener { onDelete(user) }
        }
    }

    override fun getItemCount() = users.size
}