package com.example.nutrisense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class CropSelectionActivity : AppCompatActivity() {

    private var tfliteInterpreter: Interpreter? = null
    private lateinit var cropLabels: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_selection)

        // 1. Initialize the TFLite Model and Labels
        try {
            tfliteInterpreter = Interpreter(loadModelFile())
            cropLabels = loadLabels()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading model", Toast.LENGTH_SHORT).show()
        }

        // 2. Retrieve sensor data
        val nitrogen = intent.getStringExtra("EXTRA_NITROGEN")?.toFloatOrNull() ?: 0f
        val phosphorus = intent.getStringExtra("EXTRA_PHOSPHORUS")?.toFloatOrNull() ?: 0f
        val potassium = intent.getStringExtra("EXTRA_POTASSIUM")?.toFloatOrNull() ?: 0f
        val moisture = intent.getStringExtra("EXTRA_MOISTURE")?.toFloatOrNull() ?: 0f

        // 3. Find all UI views
        val tvSuggestedCrop = findViewById<TextView>(R.id.tv_suggested_crop_result)
        val btnProceed = findViewById<MaterialButton>(R.id.btnProceedToDashboard)
        val btnWhyCrop = findViewById<MaterialButton>(R.id.btn_why_crop)
        val mcvCropOption = findViewById<MaterialCardView>(R.id.mcv_crop_option)
        val tvCropOptionName = findViewById<TextView>(R.id.tv_crop_option_name)
        val ivCropOptionIcon = findViewById<ImageView>(R.id.iv_crop_option_icon)

        // 4. Run the model
        val recommendedCrop = getRecommendedCropFromModel(nitrogen, phosphorus, potassium, moisture)

        // 5. Update UI (We pass ALL the views here so the function can see them)
        updateUIForCrop(
            tvSuggestedCrop,
            btnWhyCrop,
            mcvCropOption,
            tvCropOptionName,
            ivCropOptionIcon,
            recommendedCrop
        )

        btnProceed.setOnClickListener {
            val dashboardIntent = Intent(this, DashboardActivity::class.java)
            dashboardIntent.putExtra("SELECTED_CROP", recommendedCrop)
            startActivity(dashboardIntent)
            finish()
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("crop_recommendation.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadLabels(): List<String> {
        return assets.open("labels.txt").bufferedReader().readLines()
    }

    private fun getRecommendedCropFromModel(n: Float, p: Float, k: Float, moisture: Float): String {
        val interpreter = tfliteInterpreter ?: return "Unknown"
        val inputFeatures = floatArrayOf(n, p, k, moisture)
        val outputProbabilities = Array(1) { FloatArray(cropLabels.size) }
        interpreter.run(inputFeatures, outputProbabilities)

        val resultProbabilities = outputProbabilities[0]
        var maxIndex = 0
        var maxProb = -1f
        for (i in resultProbabilities.indices) {
            if (resultProbabilities[i] > maxProb) {
                maxProb = resultProbabilities[i]
                maxIndex = i
            }
        }
        return if (cropLabels.isNotEmpty()) cropLabels[maxIndex].replaceFirstChar { it.uppercase() } else "Unknown"
    }

    // THE FIXED FUNCTION: Added 'ivOptionIcon: ImageView' to the parameters
    private fun updateUIForCrop(
        tvSuggested: TextView,
        btnWhy: MaterialButton,
        mcvOption: MaterialCardView,
        tvOptionName: TextView,
        ivOptionIcon: ImageView,
        cropName: String
    ) {
        tvSuggested.text = cropName
        tvOptionName.text = cropName
        btnWhy.text = "Why $cropName?"

        val iconResId = when (cropName.lowercase()) {
            "rice" -> R.drawable.ic_crop_rice
            "corn" -> R.drawable.ic_leaf_outline
            else -> R.drawable.ic_plant
        }

        val drawable = ContextCompat.getDrawable(this, iconResId)
        tvSuggested.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)

        // This line will no longer be red because ivOptionIcon is now a parameter!
        ivOptionIcon.setImageDrawable(drawable)

        mcvOption.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        tfliteInterpreter?.close()
    }
}