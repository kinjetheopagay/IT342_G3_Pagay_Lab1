package com.staffguard.mobile.ui

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.staffguard.mobile.R
import com.staffguard.mobile.models.IncidentResponse

class IncidentDetailDialog(
    context: Context,
    private val incident: IncidentResponse
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_incident_detail)

        window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvStatus = findViewById<TextView>(R.id.tvDetailStatus)
        val tvDescription = findViewById<TextView>(R.id.tvDetailDescription)
        val tvSupervisor = findViewById<TextView>(R.id.tvDetailSupervisor)
        val tvDateTime = findViewById<TextView>(R.id.tvDetailDateTime)
        val ivProof = findViewById<ImageView>(R.id.ivProofImage)
        val tvNoImage = findViewById<TextView>(R.id.tvNoImage)
        val btnClose = findViewById<TextView>(R.id.btnClose)

        tvTitle.text = incident.title
        tvStatus.text = incident.status
        tvDescription.text = incident.description ?: "No description"
        tvSupervisor.text = incident.supervisor
        tvDateTime.text = "${incident.date} at ${incident.time?.take(5) ?: "--"}"

        // Status color
        val color = when (incident.status) {
            "APPROVED" -> android.graphics.Color.parseColor("#1D9E75")
            "REJECTED" -> android.graphics.Color.parseColor("#E24B4A")
            else -> android.graphics.Color.parseColor("#E85D24")
        }
        tvStatus.setBackgroundColor(color)

        // Load image if exists
        if (!incident.imageUrl.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(
                    incident.imageUrl.substringAfter("base64,"),
                    Base64.DEFAULT
                )
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivProof.setImageBitmap(bitmap)
                ivProof.visibility = android.view.View.VISIBLE
                tvNoImage.visibility = android.view.View.GONE
            } catch (e: Exception) {
                ivProof.visibility = android.view.View.GONE
                tvNoImage.visibility = android.view.View.VISIBLE
            }
        } else {
            ivProof.visibility = android.view.View.GONE
            tvNoImage.visibility = android.view.View.VISIBLE
        }

        btnClose.setOnClickListener { dismiss() }
    }
}