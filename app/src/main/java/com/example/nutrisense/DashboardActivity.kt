package com.example.nutrisense

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var backPressedTime: Long = 0
    private var backToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // UPDATED: Set status bar to green and icons to white
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.nutrisense_green)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Fetch user data automatically when Dashboard loads
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfile(userId)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    backToast?.cancel()
                    finishAffinity()
                } else {
                    backToast = Toast.makeText(this@DashboardActivity, "Press back again to exit", Toast.LENGTH_SHORT)
                    backToast?.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        })

        val flNotification = findViewById<FrameLayout>(R.id.fl_dashboard_notification)
        flNotification.setOnClickListener {
            Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show()
        }

        val ivMenu = findViewById<ImageView>(R.id.iv_dashboard_menu)
        ivMenu.setOnClickListener {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
        }

        val tvConnected = findViewById<TextView>(R.id.tv_connected_status)
        tvConnected.setOnClickListener {
            Toast.makeText(this, "System is online and connected", Toast.LENGTH_SHORT).show()
        }

        // --- AUTOMATED SPRINKLER TOGGLE LOGIC ---
        val switchSprinkler = findViewById<SwitchMaterial>(R.id.switch_sprinkler)
        val tvSprinklerStatus = findViewById<TextView>(R.id.tv_sprinkler_status)

        // Helper function to update the text and colors dynamically
        fun updateSprinklerUI(isOn: Boolean) {
            if (isOn) {
                tvSprinklerStatus.text = "System Active - ON"
                // Blue thumb, lighter blue track
                switchSprinkler.thumbTintList = ColorStateList.valueOf(Color.parseColor("#4285F4"))
                switchSprinkler.trackTintList = ColorStateList.valueOf(Color.parseColor("#AECBFA"))
            } else {
                tvSprinklerStatus.text = "System Inactive - OFF"
                // Gray thumb, lighter gray track
                switchSprinkler.thumbTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
                switchSprinkler.trackTintList = ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
            }
        }

        // 1. Apply the correct color and text immediately when the screen opens
        updateSprinklerUI(switchSprinkler.isChecked)

        // 2. Listen for the user tapping the switch to change it
        switchSprinkler.setOnCheckedChangeListener { _, isChecked ->
            updateSprinklerUI(isChecked)

            // TODO: Add logic here to send the ON/OFF command to your IoT sprinkler hardware
        }
    }

    private fun loadUserProfile(userId: String) {
        val tvName = findViewById<TextView>(R.id.tv_profile_name)
        val tvId = findViewById<TextView>(R.id.tv_profile_id)

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Keys must match what we saved in RegisterActivity
                    val fullName = document.getString("fullName")
                    val farmerId = document.getString("farmerId")

                    // Update UI (Handles nulls gracefully just in case)
                    tvName.text = fullName ?: "Unknown User"
                    tvId.text = "ID: ${farmerId ?: "N/A"}"
                } else {
                    // DIAGNOSTIC CHECK 1: The document didn't save during registration
                    Toast.makeText(this, "Error: User data not found in database!", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                // DIAGNOSTIC CHECK 2: Firebase rules are blocking the read
                Toast.makeText(this, "Firebase Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}