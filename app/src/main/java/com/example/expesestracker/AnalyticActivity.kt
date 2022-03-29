package com.example.expesestracker

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.expesestracker.models.DBUtilities
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate





class AnalyticActivity : AppCompatActivity() {

    private var util = DBUtilities()
    var hashMap: HashMap<String, Float> = HashMap()
    var typeSelection = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytic)

        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val spinner = findViewById<Spinner>(R.id.selectTimeSpinner)

        val legend = pieChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.textSize = 16F

        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelTextSize(0F)
        pieChart.setCenterTextSize(16F)

        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                hashMap = getDBData(position)
                when(position){
                    0 -> {
                        pieChart.centerText = "This Weeks Spending"
                    }
                    1 -> {
                        pieChart.centerText = "This Months Spending"
                    }
                    2 -> {
                        pieChart.centerText = "This Weeks Incomes"
                    }
                    3 -> {
                        pieChart.centerText = "This Months Incomes"
                    }
                }
                if (hashMap.isNotEmpty()){
                    val entries = getPieData(hashMap)
                    val dataset = PieDataSet(entries, typeSelection)
                    dataset.colors = colors

                    val data = PieData(dataset)
                    data.setDrawValues(true)
                    data.setValueFormatter(PercentFormatter(pieChart))
                    data.setValueTextSize(12f)
                    data.setValueTextColor(Color.BLUE)

                    pieChart.data = data
                    pieChart.invalidate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }

    private fun getPieData(values : HashMap<String, Float>): ArrayList<PieEntry> {
        val entries: ArrayList<PieEntry> = ArrayList()
        for (key in values.keys) {
            if (key.equals("Total")){
            }
            else{
                entries.add(PieEntry(values[key]!!, key))
            }
        }
        return entries
    }

    private fun getDBData(position: Int): HashMap<String, Float> {
        var hashMap : HashMap<String, Float> = HashMap()
        when(position){
            0 -> hashMap = util.getThisWeekExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
            1 -> hashMap = util.getThisMonthExpenses(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))
            2 -> hashMap = util.getThisWeekIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE))
            3 -> hashMap = util.getThisMonthIncomes(this.getSharedPreferences("test_income", Context.MODE_PRIVATE))
        }
        return hashMap

    }
}