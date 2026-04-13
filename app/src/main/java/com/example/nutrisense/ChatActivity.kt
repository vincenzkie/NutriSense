package com.example.nutrisense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip

class ChatActivity : AppCompatActivity() {

    private lateinit var llFaqToggle: LinearLayout
    private lateinit var llFaqItems: LinearLayout
    private lateinit var ivFaqExpandArrow: ImageView
    private var isFaqExpanded = false

    // FAQ data: question to answer
    private val faqData = listOf(
        "How does NutriSense AI analyze my nutrition?" to
            "NutriSense AI uses advanced machine learning to analyze your dietary intake, cross-referencing nutrients against recommended daily values and your personal health profile to surface personalized insights.",
        "What nutrients does NutriSense track?" to
            "NutriSense tracks macronutrients (carbs, proteins, fats), vitamins (A, B-complex, C, D, E, K), essential minerals (calcium, iron, magnesium, potassium, zinc), and key micronutrients such as fiber and omega-3s.",
        "How accurate are the AI recommendations?" to
            "Recommendations are grounded in peer-reviewed research and WHO/USDA guidelines. Accuracy improves over time as the AI learns from your logged data and feedback.",
        "Can I track specific diet types (keto, vegan, etc.)?" to
            "Yes! NutriSense supports ketogenic, vegan, vegetarian, paleo, Mediterranean, gluten-free, and many more dietary preferences, customizing all recommendations to your chosen plan.",
        "How often should I check my nutrient levels?" to
            "Log your meals daily for real-time tracking. Review your weekly nutrient summaries to spot patterns or deficiencies, and consult your healthcare provider if you have specific health conditions."
    )

    private val faqItemIds = listOf(
        R.id.faq_item_1,
        R.id.faq_item_2,
        R.id.faq_item_3,
        R.id.faq_item_4,
        R.id.faq_item_5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        llFaqToggle = findViewById(R.id.ll_faq_toggle)
        llFaqItems = findViewById(R.id.ll_faq_items)
        ivFaqExpandArrow = findViewById(R.id.iv_faq_expand_arrow)

        setupFaqItems()
        setupFaqToggle()
        setupQuickReplies()
    }

    private fun setupFaqItems() {
        faqItemIds.forEachIndexed { index, viewId ->
            val faqView = findViewById<View>(viewId)
            val (question, answer) = faqData[index]

            val tvQuestion = faqView.findViewById<TextView>(R.id.tv_faq_question)
            val tvAnswer = faqView.findViewById<TextView>(R.id.tv_faq_answer)
            val divider = faqView.findViewById<View>(R.id.divider_faq)
            val ivArrow = faqView.findViewById<ImageView>(R.id.iv_faq_arrow)
            val questionRow = faqView.findViewById<View>(R.id.ll_faq_question_row)

            tvQuestion.text = question
            tvAnswer.text = answer

            questionRow.setOnClickListener {
                val isExpanded = tvAnswer.visibility == View.VISIBLE
                tvAnswer.visibility = if (isExpanded) View.GONE else View.VISIBLE
                divider.visibility = if (isExpanded) View.GONE else View.VISIBLE
                ivArrow.rotation = if (isExpanded) 90f else 270f
            }
        }
    }

    private fun setupFaqToggle() {
        llFaqToggle.setOnClickListener {
            isFaqExpanded = !isFaqExpanded
            llFaqItems.visibility = if (isFaqExpanded) View.VISIBLE else View.GONE

            val fromDegree = if (isFaqExpanded) 90f else 270f
            val toDegree = if (isFaqExpanded) 270f else 90f
            val rotate = RotateAnimation(
                fromDegree, toDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 200
                fillAfter = true
            }
            ivFaqExpandArrow.startAnimation(rotate)
        }
    }

    private fun setupQuickReplies() {
        val chipIds = listOf(
            R.id.chip_quick_reply_1,
            R.id.chip_quick_reply_2,
            R.id.chip_quick_reply_3,
            R.id.chip_quick_reply_4,
            R.id.chip_quick_reply_5
        )
        chipIds.forEach { chipId ->
            val chip = findViewById<Chip>(chipId)
            chip.setOnClickListener {
                val etInput = findViewById<android.widget.EditText>(R.id.et_chat_input)
                etInput.setText(chip.text)
                etInput.setSelection(etInput.text.length)
            }
        }

        val ivSend = findViewById<ImageView>(R.id.iv_chat_send)
        ivSend.setOnClickListener {
            val etInput = findViewById<android.widget.EditText>(R.id.et_chat_input)
            val message = etInput.text.toString().trim()
            if (message.isNotEmpty()) {
                etInput.text.clear()
            }
        }
    }
}
