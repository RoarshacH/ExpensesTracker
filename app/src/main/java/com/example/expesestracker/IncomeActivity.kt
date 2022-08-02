package com.example.expesestracker

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.expesestracker.models.BuildNotifications
import com.example.expesestracker.models.DBUtilities
import com.example.expesestracker.models.SQLUtilities
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class IncomeActivity : AppCompatActivity() {


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

        builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        with(builder) {
            setTitle(getText(R.string.add_expense_item))
            setView(view)
            saveButton.setOnClickListener {
                val expenseAmount: String = amount.text.toString()

                if (TextUtils.isEmpty(expenseAmount)) {
                    amount.error =  getText(R.string.amount_error)
                    return@setOnClickListener
                }
                val expenseAmountFLOAT: Float? = expenseAmount.toFloatOrNull()
                val expenseItem = itemSpinner.selectedItem.toString()
                val expenseDescription = description.text.toString()

                if (expenseItem == getText(R.string.select_item)) {
                    Toast.makeText(
                        view.context,
                        getText(R.string.select_type),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
                    val databaseClass = SQLUtilities(this@IncomeActivity)
                    val notifications = BuildNotifications(this@IncomeActivity);
                    val result: Boolean = databaseClass.InsertItem(expenseItem, expenseDescription, dateTime, expenseAmountFLOAT, 0)
                    if (result) {
                        Toast.makeText(view.context, getText(R.string.add_item_success), Toast.LENGTH_SHORT)
                            .show()
                        var totalExp = databaseClass.getTotalForWeek(1)
                        val totalIncome = databaseClass.getTotalForWeek(0)
                        if (totalIncome >= (totalExp + 500) ){
                            notifications.buildNotification(getText(R.string.expense_notification_title) as String?, getText(R.string.income_notification_message).toString())
                        }
                    } else {
                        Toast.makeText(view.context, getText(R.string.error_adding_item), Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
            setCancelable(true)
            show()
        }
    }

}