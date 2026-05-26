package com.staffguard.mobile.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.staffguard.mobile.R
import com.staffguard.mobile.api.ApiClient
import com.staffguard.mobile.api.ApiService
import com.staffguard.mobile.models.IncidentRequest
import com.staffguard.mobile.models.IncidentResponse
import com.staffguard.mobile.models.User
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
    private var allUsers: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_incident)

        val btnBack = findViewById<TextView>(R.id.btnBack)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spinnerSupervisor = findViewById<Spinner>(R.id.spinnerSupervisor)
        val etDate = findViewById<EditText>(R.id.etDate)
        val layoutImageUpload = findViewById<LinearLayout>(R.id.layoutImageUpload)
        val ivPreview = findViewById<ImageView>(R.id.ivPreview)
        val btnRemoveImage = findViewById<Button>(R.id.btnRemoveImage)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val tvMessage = findViewById<TextView>(R.id.tvMessage)

        val token = TokenManager.getBearerToken(this)
        val apiService = ApiClient.retrofit.create(ApiService::class.java)

        // Load users for supervisor spinner
        apiService.getAllUsers(token).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    allUsers = response.body() ?: emptyList()
                    val names = allUsers.map { it.name }
                    val adapter = ArrayAdapter(
                        this@SubmitIncidentActivity,
                        android.R.layout.simple_spinner_item,
                        names
                    ).also {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinnerSupervisor.adapter = adapter
                }
            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(
                    this@SubmitIncidentActivity,
                    "Failed to load users",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Set today's date
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        etDate.setText(today)

        // Date picker on tap
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

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
            val date = etDate.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (allUsers.isEmpty()) {
                Toast.makeText(this, "Please wait for users to load", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected supervisor name
            val supervisorName = allUsers[spinnerSupervisor.selectedItemPosition].name
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val request = IncidentRequest(
                title = title,
                description = description,
                supervisor = supervisorName,
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
                        tvMessage.setBackgroundColor(
                            android.graphics.Color.parseColor("#1D9E75")
                        )
                        tvMessage.visibility = View.VISIBLE
                        etTitle.setText("")
                        etDescription.setText("")
                        imageBase64 = null
                        ivPreview.visibility = View.GONE
                        btnRemoveImage.visibility = View.GONE
                        layoutImageUpload.visibility = View.VISIBLE
                    } else {
                        tvMessage.text = "❌ Failed to submit incident"
                        tvMessage.setBackgroundColor(
                            android.graphics.Color.parseColor("#E24B4A")
                        )
                        tvMessage.visibility = View.VISIBLE
                    }
                }
                override fun onFailure(call: Call<IncidentResponse>, t: Throwable) {
                    btnSubmit.isEnabled = true
                    btnSubmit.text = "SUBMIT"
                    Toast.makeText(
                        this@SubmitIncidentActivity,
                        "Cannot connect to server",
                        Toast.LENGTH_SHORT
                    ).show()
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