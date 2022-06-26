package com.example.expesestracker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.expesestracker.models.*

class MainActivity : AppCompatActivity() {

    private val util = DBUtilities()
    private var latestExpense: Float = 0.0F
    private var totalExpense: Float = 0.0F
    private var totalIncome: Float = 0.0F
    private var latestIncome:Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setValuesToTable()
        val scheduleNotification = ScheduleNotification(this, applicationContext)
        scheduleNotification.scheduleNotification();

        val expensesButton = findViewById<ImageButton>(R.id.expensesImageButton)
        expensesButton.setOnClickListener {
            val intent = Intent(this, ExpensesActivity::class.java)
            startActivity(intent)
        }


        val incomesButton = findViewById<ImageButton>(R.id.incomesImageButton)
        incomesButton.setOnClickListener {
            val intent = Intent(this, IncomeActivity::class.java)
            startActivity(intent)
        }

        val reportButton = findViewById<ImageButton>(R.id.reportImageButton)
        reportButton.setOnClickListener {
            val intent = Intent(this, ReportsActivity::class.java)
            startActivity(intent)
        }

        val analyticsButton = findViewById<ImageButton>(R.id.analyticsImageButton)
        analyticsButton.setOnClickListener {
            val intent = Intent(this, AnalyticActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setValuesToTable()
    }

    @SuppressLint("SetTextI18n")
    private fun setValuesToTable(){

        val expensesTotalTextView = findViewById<TextView>(R.id.expensesTotal)
        val expensesLatestTextView = findViewById<TextView>(R.id.expensesLatest)
        val incomeLatestTextView = findViewById<TextView>(R.id.incomeLatest)
        val incomeTotalTextView = findViewById<TextView>(R.id.incomeTotal)
        var sqlUtil = SQLUtilities(this)
        latestExpense = sqlUtil.getLatestItem(1)
        totalExpense = sqlUtil.getTotalForWeek(1)

        latestIncome =  sqlUtil.getLatestItem(0)
        totalIncome =  sqlUtil.getTotalForWeek(0)

        val hMap = sqlUtil.getThisMonthExpenses()
        for ((key, value) in hMap) {
            Log.i("TAG","$key = $value")
        }
        Log.i("TAG","DONE")

        expensesLatestTextView.text = "$ $latestExpense"
        expensesTotalTextView.text = "$ $totalExpense"
        incomeLatestTextView.text = "$ $latestIncome"
        incomeTotalTextView.text = "$ $totalIncome"

    }
}