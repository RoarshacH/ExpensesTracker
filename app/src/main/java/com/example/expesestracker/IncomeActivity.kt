package com.example.expesestracker

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.expesestracker.models.DBUtilities
import com.example.expesestracker.models.ExpenseItem
import com.example.expesestracker.models.SQLUtilities
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class IncomeActivity : AppCompatActivity() {

    private val util = DBUtilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)


        val fab = findViewById<FloatingActionButton>(R.id.fabAddIncome)
        fab.setOnClickListener {
            addItem()
        }
    }

    private fun addItem() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.incomes_input_layout, null)
        val saveButton = view.findViewById<Button>(R.id.save)
        val amount = view.findViewById<EditText>(R.id.amount)
        val itemSpinner = view.findViewById<Spinner>(R.id.itemsSpinner)
        val description = view.findViewById<EditText>(R.id.note)

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })

        with(builder) {
            setTitle("Add an Income!")

            setView(view)
            saveButton.setOnClickListener {
                val expenseAmount: String = amount.text.toString()

                if (TextUtils.isEmpty(expenseAmount)) {
                    amount.error = "Amount is required"
                    return@setOnClickListener
                }
                val expenseAmountFLOAT: Float? = expenseAmount.toFloatOrNull()
                val expenseItem = itemSpinner.selectedItem.toString()
                val expenseDescription = description.text.toString()

                if (expenseItem == "Select Item") {
                    Toast.makeText(
                        view.context,
                        "Select a Type",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
                    val databaseClass = SQLUtilities(this@IncomeActivity)
                    val result: Boolean = databaseClass.InsertItem(expenseItem, expenseDescription, dateTime, expenseAmountFLOAT, 0)
                    if (result) {
                        Toast.makeText(view.context, "Item added successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(view.context, "Error Adding Item", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
            setCancelable(true)
            show()
        }
    }
}