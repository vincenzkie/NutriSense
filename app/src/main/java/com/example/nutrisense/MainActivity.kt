package com.example.nutrisense

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Tell the app to draw under the system windows (edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 2. Make the status bar background completely transparent
        window.statusBarColor = Color.TRANSPARENT
        // 3. Make the status bar icons (battery, Wi-Fi, time) dark so they show up on light backgrounds
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        // This MUST come after the window setup above
        setContentView(R.layout.activity_main)

        // Find the Get Started button and set the click action
        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener {

            // --- ADDED FOR TESTING ---
            // This logs you out so you don't automatically bypass the Login UI
            FirebaseAuth.getInstance().signOut()

            // This line opens your LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}