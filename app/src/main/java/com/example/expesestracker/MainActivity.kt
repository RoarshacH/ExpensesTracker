package com.example.expesestracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expesestracker.models.BuildNotifications
import com.example.expesestracker.models.SQLUtilities
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var latestExpense: Float = 0.0F
    private var totalExpense: Float = 0.0F
    private var totalIncome: Float = 0.0F
    private var latestIncome:Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setValuesToTable()

        var noti = BuildNotifications(this@MainActivity);
        noti.buildScheduledNotification(noti.getNotification( "DelayNOTI", "Other" ) )

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

        val cameraButton = findViewById<FloatingActionButton>(R.id.imageFab)
        cameraButton.setOnClickListener{
            val intent = Intent(this, PhotosActivity::class.java)
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
        val sqlUtil = SQLUtilities(this)
        latestExpense = sqlUtil.getLatestItem(1)
        totalExpense = sqlUtil.getTotalForWeek(1)

        latestIncome =  sqlUtil.getLatestItem(0)
        totalIncome =  sqlUtil.getTotalForWeek(0)

        expensesLatestTextView.text = "$ $latestExpense"
        expensesTotalTextView.text = "$ $totalExpense"
        incomeLatestTextView.text = "$ $latestIncome"
        incomeTotalTextView.text = "$ $totalIncome"

    }
}