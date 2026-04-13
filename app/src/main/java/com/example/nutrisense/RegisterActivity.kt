package com.example.nutrisense

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    // Declare Firebase Auth and Firestore variables
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Tell the app to draw under the system windows (edge-to-edge)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 2. Make the status bar background completely transparent
        window.statusBarColor = Color.TRANSPARENT
        // 3. Make the status bar icons (battery, Wi-Fi, time) dark so they show up on light backgrounds
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // --- NAVIGATION TO LOGIN ---
        val tvLoginLink = findViewById<TextView>(R.id.tv_login_link)
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // --- REGISTRATION LOGIC ---
        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etFarmerId = findViewById<EditText>(R.id.et_farmer_id)
        val etMobileNumber = findViewById<EditText>(R.id.et_mobile_number)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val farmerId = etFarmerId.text.toString().trim()
            val mobileNumber = etMobileNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || farmerId.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            // 1. Create a map of the data to save to Firestore
                            // Ensure these keys match exactly what the Dashboard expects!
                            val userMap = hashMapOf(
                                "fullName" to fullName,
                                "farmerId" to farmerId,
                                "mobileNumber" to mobileNumber,
                                "email" to email
                            )

                            // 2. Save to Firestore under the "users" collection
                            db.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registration Successful! Please log in.", Toast.LENGTH_SHORT).show()

                                    // Sign out the user and force them to log in
                                    auth.signOut()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}