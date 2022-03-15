package com.example.expesestracker

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.expesestracker.models.DBUtilities
import com.example.expesestracker.models.ExpenseItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ExpensesActivity : AppCompatActivity() {

    val util = DBUtilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)


        util.getValues(this.getSharedPreferences("test-expenses", Context.MODE_PRIVATE))

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            addItem()
        }

    }

    private fun addItem() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.expenses_input_layout, null)
        val saveButton = view.findViewById<Button>(R.id.save)
        val amount = view.findViewById<EditText>(R.id.amount)
        val itemSpinner = view.findViewById<Spinner>(R.id.itemsSpinner)
        val description = view.findViewById<EditText>(R.id.note)

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })

        with(builder) {
            setTitle("Add an Expense Item")

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
                    val expense = ExpenseItem("99", expenseItem, Date().toString() , expenseDescription,expenseAmountFLOAT!! )
                    util.saveItem(view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE), "EXPENSE", expense)
                    finish()
                }
            }
            setCancelable(true)
            show()
        }
    }
}