package com.staffguard.mobile.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.ProfilePictureRequest
import com.staffguard.mobile.models.User
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivAvatar: ImageView
    private lateinit var tvAvatarPlaceholder: TextView
    private lateinit var tvSuccess: TextView
    private val PICK_IMAGE_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ivAvatar = findViewById(R.id.ivAvatar)
        tvAvatarPlaceholder = findViewById(R.id.tvAvatarPlaceholder)
        tvSuccess = findViewById(R.id.tvSuccess)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            TokenManager.clearAll(this)
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        findViewById<Button>(R.id.btnChangePhoto).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Load user profile
        apiService.getMe(token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        findViewById<TextView>(R.id.tvName).text = it.name
                        findViewById<TextView>(R.id.tvRoleSub).text = it.role
                        findViewById<TextView>(R.id.tvEmployeeId).text = it.employeeId ?: "N/A"
                        findViewById<TextView>(R.id.tvEmail).text = it.email
                        findViewById<TextView>(R.id.tvRole).text = it.role

                        // Show profile picture if exists
                        if (!it.profilePicture.isNullOrEmpty()) {
                            showBase64Image(it.profilePicture)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

            // Compress to match web — 200x200 max, JPEG 30% quality
            val compressed = compressBitmap(bitmap)
            val base64 = bitmapToBase64(compressed)
            val dataUrl = "data:image/jpeg;base64,$base64"

            // Show preview immediately
            showBase64Image(dataUrl)

            // Upload to backend
            val token = TokenManager.getBearerToken(this)
            val apiService = ApiClient.retrofit.create(ApiService::class.java)

            apiService.updateProfilePicture(token, ProfilePictureRequest(dataUrl))
                .enqueue(object : Callback<Any> {
                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        if (response.isSuccessful) {
                            tvSuccess.visibility = View.VISIBLE
                            tvSuccess.postDelayed({
                                tvSuccess.visibility = View.GONE
                            }, 3000)
                        } else {
                            Toast.makeText(this@ProfileActivity, "Failed to update photo", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        Toast.makeText(this@ProfileActivity, "Connection error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 200
        val width = bitmap.width
        val height = bitmap.height
        val scale = if (width > height) maxSize.toFloat() / width else maxSize.toFloat() / height
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun showBase64Image(base64: String) {
        try {
            val pureBase64 = if (base64.contains(",")) base64.split(",")[1] else base64
            val bytes = Base64.decode(pureBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ivAvatar.setImageBitmap(bitmap)
            ivAvatar.visibility = View.VISIBLE
            tvAvatarPlaceholder.visibility = View.GONE
        } catch (e: Exception) {
            ivAvatar.visibility = View.GONE
            tvAvatarPlaceholder.visibility = View.VISIBLE
        }
    }
}