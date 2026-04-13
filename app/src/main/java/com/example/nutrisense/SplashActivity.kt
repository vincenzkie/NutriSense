package com.example.nutrisense

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Tell the app to draw under the system windows (edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 2. Make the status bar background completely transparent
        window.statusBarColor = Color.TRANSPARENT
        // 3. Make the status bar icons (battery, Wi-Fi, time) dark so they show up on white
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        // This MUST come after the window setup above
        setContentView(R.layout.activity_splash)

        val mainLogo = findViewById<ImageView>(R.id.mainLogo)
        val appNameText = findViewById<TextView>(R.id.appNameText)
        val leaf1 = findViewById<ImageView>(R.id.leaf1)
        val leaf2 = findViewById<ImageView>(R.id.leaf2)
        val leaf3 = findViewById<ImageView>(R.id.leaf3)

        // 1. Animate the Main Logo
        mainLogo.animate()
            .alpha(1f)
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(1200)
            .start()

        // 2. Animate the Nutrisense Text (Fades in and slides up slightly)
        appNameText.translationY = 50f
        appNameText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(1000)
            .setStartDelay(500)
            .start()

        // 3. Animate Leaf 1
        leaf1.animate()
            .alpha(0.8f)
            .translationY(400f)
            .translationX(150f)
            .rotation(200f)
            .setDuration(2500)
            .setStartDelay(200)
            .start()

        // 4. Animate Leaf 2
        leaf2.animate()
            .alpha(0.6f)
            .translationY(500f)
            .translationX(-200f)
            .rotation(-150f)
            .setDuration(2000)
            .setStartDelay(400)
            .start()

        // 5. Animate Leaf 3
        leaf3.animate()
            .alpha(0.7f)
            .translationY(-300f)
            .translationX(100f)
            .rotation(120f)
            .setDuration(2800)
            .setStartDelay(100)
            .start()

        // 6. Navigate to MainActivity after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000)
    }
}