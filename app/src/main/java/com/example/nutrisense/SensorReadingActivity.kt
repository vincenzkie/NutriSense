package com.example.nutrisense

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class SensorReadingActivity : AppCompatActivity() {

    // Variables to store current values so we can pass them to the next activity
    private var currentN = 0
    private var currentP = 0
    private var currentK = 0
    private var currentM = 0

    // Handler to manage the "Live" update loop
    private val mainHandler = Handler(Looper.getMainLooper())

    private lateinit var tvNitrogen: TextView
    private lateinit var tvPhosphorus: TextView
    private lateinit var tvPotassium: TextView
    private lateinit var tvMoisture: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.WHITE
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContentView(R.layout.activity_sensor_reading)

        // 1. Initialize UI Elements
        tvNitrogen = findViewById(R.id.tv_nitrogen_value)
        tvPhosphorus = findViewById(R.id.tv_phosphorus_value)
        tvPotassium = findViewById(R.id.tv_potassium_value)
        tvMoisture = findViewById(R.id.tv_moisture_value)
        val btnNextToCrop = findViewById<MaterialButton>(R.id.btnNextToCrop)

        // 2. Start the "Live" update loop
        mainHandler.post(updateSensorsTask)

        // 3. Navigation
        btnNextToCrop.setOnClickListener {
            val intent = Intent(this, CropSelectionActivity::class.java)

            // Pass the LATEST values that were generated
            intent.putExtra("EXTRA_NITROGEN", currentN.toString())
            intent.putExtra("EXTRA_PHOSPHORUS", currentP.toString())
            intent.putExtra("EXTRA_POTASSIUM", currentK.toString())
            intent.putExtra("EXTRA_MOISTURE", currentM.toString())

            startActivity(intent)
        }
    }

    // This is the "Engine" that runs every 1.5 seconds
    private val updateSensorsTask = object : Runnable {
        override fun run() {
            // Generate random values
            currentN = Random.nextInt(20, 140)
            currentP = Random.nextInt(10, 80)
            currentK = Random.nextInt(10, 200)
            currentM = Random.nextInt(15, 95)

            // Update the UI
            tvNitrogen.text = currentN.toString()
            tvPhosphorus.text = currentP.toString()
            tvPotassium.text = currentK.toString()
            tvMoisture.text = currentM.toString()

            // Re-run this task again in 1500 milliseconds (1.5 seconds)
            mainHandler.postDelayed(this, 1500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the loop when the activity is closed to save battery
        mainHandler.removeCallbacks(updateSensorsTask)
    }
}