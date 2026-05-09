package com.staffguard.mobile.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.IncidentRequest
import com.staffguard.mobile.models.IncidentResponse
import com.staffguard.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SubmitIncidentActivity : AppCompatActivity() {

    private var imageBase64: String? = null
    private val IMAGE_PICK_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_incident)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etSupervisor = findViewById<EditText>(R.id.etSupervisor)
        val etDate = findViewById<EditText>(R.id.etDate)
        val layoutImageUpload = findViewById<LinearLayout>(R.id.layoutImageUpload)
        val ivPreview = findViewById<ImageView>(R.id.ivPreview)
        val btnRemoveImage = findViewById<Button>(R.id.btnRemoveImage)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        // Set today's date
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(today)

        btnBack.setOnClickListener { finish() }
        btnCancel.setOnClickListener { finish() }

        // Image upload
        layoutImageUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        btnRemoveImage.setOnClickListener {
            imageBase64 = null
            ivPreview.visibility = View.GONE
            btnRemoveImage.visibility = View.GONE
            layoutImageUpload.visibility = View.VISIBLE
        }

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val supervisor = etSupervisor.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || supervisor.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = TokenManager.getBearerToken(this)
            val apiService = ApiClient.retrofit.create(ApiService::class.java)
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val request = IncidentRequest(
                title = title,
                description = description,
                supervisor = supervisor,
                date = date,
                time = time,
                imageUrl = imageBase64
            )

            btnSubmit.isEnabled = false
            btnSubmit.text = "Submitting..."

            apiService.submitIncident(token, request).enqueue(object : Callback<IncidentResponse> {
                override fun onResponse(
                    call: Call<IncidentResponse>,
                    response: Response<IncidentResponse>
                ) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "SUBMIT"
                    if (response.isSuccessful) {
                        tvMessage.text = "✅ Incident submitted successfully!"
                        tvMessage.setBackgroundColor(android.graphics.Color.parseColor("#1D9E75"))
                        tvMessage.visibility = View.VISIBLE
                        etTitle.setText("")
                        etDescription.setText("")
                        etSupervisor.setText("")
                        imageBase64 = null
                        ivPreview.visibility = View.GONE
                        btnRemoveImage.visibility = View.GONE
                        layoutImageUpload.visibility = View.VISIBLE
                    } else {
                        tvMessage.text = "❌ Failed to submit incident"
                        tvMessage.setBackgroundColor(android.graphics.Color.parseColor("#E24B4A"))
                        tvMessage.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<IncidentResponse>, t: Throwable) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "SUBMIT"
                    Toast.makeText(this@SubmitIncidentActivity,
                        "Cannot connect to server", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                val ivPreview = findViewById<ImageView>(R.id.ivPreview)
                val btnRemoveImage = findViewById<Button>(R.id.btnRemoveImage)
                val layoutImageUpload = findViewById<LinearLayout>(R.id.layoutImageUpload)

                // Compress and convert to Base64
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val compressed = compressBitmap(bitmap)
                imageBase64 = bitmapToBase64(compressed)

                ivPreview.setImageBitmap(compressed)
                ivPreview.visibility = View.VISIBLE
                btnRemoveImage.visibility = View.VISIBLE
                layoutImageUpload.visibility = View.GONE
            }
        }
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val maxSize = 800
        val width = bitmap.width
        val height = bitmap.height
        val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        return if (scale < 1) {
            Bitmap.createScaledBitmap(bitmap,
                (width * scale).toInt(),
                (height * scale).toInt(), true)
        } else bitmap
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val bytes = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}