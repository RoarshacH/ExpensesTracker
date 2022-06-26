package com.example.expesestracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.expesestracker.models.SQLUtilities
import java.util.HashMap

class ReportsActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        var sqlUtil = SQLUtilities(this)

        val hashMapExpensesWeek : HashMap<String, Float> = sqlUtil.getThisWeekExpenses()
        fillTable(hashMapExpensesWeek, "ThisWeek", "Expenses")

        val hashMapExpensesMonth : HashMap<String, Float> = sqlUtil.getThisMonthExpenses()
        fillTable(hashMapExpensesMonth, "Month", "Expenses")

        val hashMapIncomesMonth : HashMap<String, Float> = sqlUtil.getThisMonthIncomes()
        fillTable(hashMapIncomesMonth, "Month" ,"Incomes")

        val hashMapIncomesWeek : HashMap<String, Float> = sqlUtil.getThisWeekIncomes()
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