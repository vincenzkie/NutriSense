package com.example.nutrisense

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FaqActivity : AppCompatActivity() {

    private lateinit var rvFaqList: RecyclerView
    private lateinit var ivFaqBack: ImageView

    data class FaqItem(val question: String, val answer: String, var isExpanded: Boolean = false)

    private val faqItems = listOf(
        FaqItem(
            question = "How does NutriSense AI analyze my nutrition?",
            answer = "NutriSense AI uses advanced machine learning to analyze your dietary intake by examining the nutrients in the foods you log. It cross-references your data against recommended daily values, identifies nutritional gaps, and provides personalized insights based on your health profile and goals."
        ),
        FaqItem(
            question = "What nutrients does NutriSense track?",
            answer = "NutriSense tracks a comprehensive range of nutrients including macronutrients (carbohydrates, proteins, fats), vitamins (A, B1–B12, C, D, E, K), essential minerals (calcium, iron, magnesium, potassium, zinc, sodium), and key micronutrients such as fiber, omega-3 fatty acids, and antioxidants."
        ),
        FaqItem(
            question = "How accurate are the AI recommendations?",
            answer = "Our AI recommendations are based on peer-reviewed nutritional research and established dietary guidelines (WHO, USDA). The system continuously learns from your feedback and logged data to refine suggestions. Accuracy improves over time as the AI builds a more complete picture of your individual nutritional needs."
        ),
        FaqItem(
            question = "Can I track specific diet types (keto, vegan, etc.)?",
            answer = "Yes! NutriSense supports a wide variety of dietary preferences including ketogenic, vegan, vegetarian, paleo, Mediterranean, gluten-free, and more. You can set your diet type in your profile and the AI will tailor all recommendations, macro targets, and food suggestions accordingly."
        ),
        FaqItem(
            question = "How often should I check my nutrient levels?",
            answer = "For best results, we recommend logging your meals daily so NutriSense can provide real-time nutrient tracking. Weekly reviews of your nutrient summaries help identify patterns and deficiencies. For users with specific health conditions, more frequent monitoring may be advised by your healthcare provider."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        rvFaqList = findViewById(R.id.rv_faq_list)
        ivFaqBack = findViewById(R.id.iv_faq_back)

        ivFaqBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        rvFaqList.layoutManager = LinearLayoutManager(this)
        rvFaqList.adapter = FaqAdapter(faqItems.toMutableList())
    }

    inner class FaqAdapter(private val items: MutableList<FaqItem>) :
        RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

        inner class FaqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val questionRow: LinearLayout = itemView.findViewById(R.id.ll_faq_question_row)
            val tvQuestion: android.widget.TextView = itemView.findViewById(R.id.tv_faq_question)
            val tvAnswer: android.widget.TextView = itemView.findViewById(R.id.tv_faq_answer)
            val divider: View = itemView.findViewById(R.id.divider_faq)
            val ivArrow: ImageView = itemView.findViewById(R.id.iv_faq_arrow)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): FaqViewHolder {
            val view = layoutInflater.inflate(R.layout.faq_item, parent, false)
            return FaqViewHolder(view)
        }

        override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
            val item = items[position]
            holder.tvQuestion.text = item.question
            holder.tvAnswer.text = item.answer

            val expandedVisibility = if (item.isExpanded) View.VISIBLE else View.GONE
            holder.tvAnswer.visibility = expandedVisibility
            holder.divider.visibility = expandedVisibility
            holder.ivArrow.rotation = if (item.isExpanded) 270f else 90f

            holder.questionRow.setOnClickListener {
                item.isExpanded = !item.isExpanded
                notifyItemChanged(position)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
