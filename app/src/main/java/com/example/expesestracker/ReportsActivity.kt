package com.example.expesestracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.expesestracker.models.DBUtilities
import java.util.HashMap

class ReportsActivity : AppCompatActivity()  {
    private var util = DBUtilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val hashMapExpensesWeek : HashMap<String, Float> = util.getThisWeekExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        fillTable(hashMapExpensesWeek, "ThisWeek", "Expenses")

        val hashMapExpensesMonth : HashMap<String, Float> = util.getThisMonthExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        fillTable(hashMapExpensesMonth, "Month", "Expenses")

        val hashMapIncomesMonth : HashMap<String, Float> = util.getThisMonthIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE))
        fillTable(hashMapIncomesMonth, "Month" ,"Incomes")

        val hashMapIncomesWeek : HashMap<String, Float> = util.getThisWeekIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE))
        fillTable(hashMapIncomesWeek, "ThisWeek" , "Incomes")
    }

    private fun fillTable(values : HashMap<String, Float>, fieldAttribute: String, uniqueForSomeFields: String){
        if (values.isNotEmpty()) {
            for (key in values.keys) {
                var textViewName = ""
                textViewName = if (key.equals("Total") || (key.equals("Other"))) {
                    key + fieldAttribute + uniqueForSomeFields
                } else {
                    key + fieldAttribute
                }

                if (textViewName.isBlank()){
                    //Do Nothing
                }
                else{
                    val textView = findViewById<TextView>(
                        resources.getIdentifier(
                            textViewName,
                            "id",
                            this.packageName
                        )
                    )
                    textView.text = if (values[key] == null) "$ 0.0" else "$ " + values[key].toString()
                }
            }
        }
    }
}