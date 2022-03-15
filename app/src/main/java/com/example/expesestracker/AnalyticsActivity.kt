package com.example.expesestracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.expesestracker.models.DBUtilities
import java.util.HashMap

class AnalyticsActivity : AppCompatActivity() {
    var util = DBUtilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)


        var hashMapExpensesWeek : HashMap<String, Float> = util.getThisWeekExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        fillTable(hashMapExpensesWeek, "ThisWeek", "Expenses")

        var hashMapExpensesMonth : HashMap<String, Float> = util.getThisMonthExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        fillTable(hashMapExpensesMonth, "Month", "Expenses")


        var hashMapIncomesMonth : HashMap<String, Float> = util.getThisMonthIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE))
        fillTable(hashMapIncomesMonth, "Month" ,"Incomes")

        var hashMapIncomesWeek : HashMap<String, Float> = util.getThisWeekIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE));
        fillTable(hashMapIncomesWeek, "ThisWeek" , "Incomes")
    }

    fun fillTable(values : HashMap<String, Float>, fieldAttribute: String, uniqueForSomeFields: String){
        if (values.isNotEmpty()) {
            for (key in values.keys) {
                var textViewName = ""
                if (key.equals("Total") || (key.equals("Other"))) {
                    textViewName = key + fieldAttribute + uniqueForSomeFields
                } else {
                    textViewName = key + fieldAttribute
                }

                if (textViewName.isNullOrBlank()){
                    //Do Nothing
                }
                else{
                    var textView = findViewById<TextView>(
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

    fun addValuesToTable(){
        var thisWeekExpenseTotal = findViewById<TextView>(R.id.TotalThisWeekExpenses)
        var thisMonthExpensesTotal = findViewById<TextView>(R.id.TotalMonthExpenses)
        thisMonthExpensesTotal.text = util.getTotal(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)).toString()

        var thisWeekIncomesTotal = findViewById<TextView>(R.id.TotalThisWeekIncomes)
        var thisMonthIncomesTotal = findViewById<TextView>(R.id.TotalMonthIncomes)
        thisMonthIncomesTotal.text = util.getTotal(getSharedPreferences("test_income", Context.MODE_PRIVATE)).toString()


    }


}