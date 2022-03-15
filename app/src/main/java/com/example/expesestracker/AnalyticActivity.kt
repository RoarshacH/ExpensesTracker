package com.example.expesestracker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expesestracker.models.DBUtilities
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate





class AnalyticActivity : AppCompatActivity() {

    var util = DBUtilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytic)

        val pieChart = findViewById<PieChart>(R.id.pieChart)

        var hashMapExpensesWeek : HashMap<String, Float> = util.getThisWeekExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
        if (hashMapExpensesWeek.isNotEmpty()) {
            val entries: ArrayList<PieEntry> = ArrayList()

            for (key in hashMapExpensesWeek.keys) {
                if (key.equals("Total")){
                }
                else{
                    entries.add(PieEntry(hashMapExpensesWeek[key]!!, key))
                }
            }

            val colors: ArrayList<Int> = ArrayList()

            for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
            for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

            val dataset = PieDataSet(entries, "Expenses")
            dataset.colors = colors

            val data = PieData(dataset)
            data.setDrawValues(true)
            data.setValueFormatter(PercentFormatter(pieChart))
            data.setValueTextSize(12f)
            data.setValueTextColor(Color.BLUE)

            pieChart.data = data
            pieChart.invalidate()

            pieChart.isDrawHoleEnabled = true
            pieChart.setUsePercentValues(true)

        }



    }
}