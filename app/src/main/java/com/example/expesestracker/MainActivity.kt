package com.example.expesestracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.example.expesestracker.models.DBUtilities

class MainActivity : AppCompatActivity() {

    val util = DBUtilities()
    var latestExpense: Float = 0.0F
    var totalExpense: Float = 0.0F
    var totalIncome: Float = 0.0F
    var latestIncome:Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setValuesToTable()

        val expensesButton = findViewById<ImageButton>(R.id.expensesImageButton)
        expensesButton.setOnClickListener { view ->
            val intent = Intent(this, ExpensesActivity::class.java)
            startActivity(intent)
        }


        val incomesButton = findViewById<ImageButton>(R.id.incomesImageButton)
        incomesButton.setOnClickListener { view ->
            val intent = Intent(this, IncomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setValuesToTable()
    }

    fun setValuesToTable(){
        val expensesTotalTextView = findViewById<TextView>(R.id.expensesTotal)
        val expensesLatestTextView = findViewById<TextView>(R.id.expensesLatest)
        val incomeLatestTextView = findViewById<TextView>(R.id.incomeLatest)
        val incomeTotalTextView = findViewById<TextView>(R.id.incomeTotal)
        latestExpense = util.getLatest(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        totalExpense = util.getTotal(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))

        latestIncome =  util.getLatest(getSharedPreferences("test_income", Context.MODE_PRIVATE))
        totalIncome = util.getTotal(getSharedPreferences("test_income", Context.MODE_PRIVATE))


        expensesLatestTextView.text = "$ " + latestExpense.toString()
        expensesTotalTextView.text = "$ " + totalExpense.toString()
        incomeLatestTextView.text = "$ " + latestIncome.toString()
        incomeTotalTextView.text = "$ " + totalIncome.toString()

    }
}