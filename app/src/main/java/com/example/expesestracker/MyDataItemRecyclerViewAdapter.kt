package com.example.expesestracker

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.expesestracker.databinding.FragmentItemBinding
import com.example.expesestracker.models.DBUtilities
import com.example.expesestracker.models.ExpenseItem
import com.example.expesestracker.models.PopulateContentForList
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [ExpenseItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyDataItemRecyclerViewAdapter(
    private val values: List<ExpenseItem>
) : RecyclerView.Adapter<MyDataItemRecyclerViewAdapter.ViewHolder>() {
    var util = DBUtilities()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.TYPE
        holder.dateView.text = item.DATE_TIME
        holder.amountView.text = item.AMOUNT.toString()


        holder.deleteItem.setOnClickListener { view ->
            var sharedPrefs =  view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
            val pageContext = view.context.javaClass.toString()

            if (pageContext == "class com.example.expesestracker.ExpensesActivity") {
                sharedPrefs = view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
            } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                sharedPrefs =  view.context.getSharedPreferences("test_income", Context.MODE_PRIVATE)
            }
            util.deleteItem(sharedPrefs, "EXPENSE", item)
            PopulateContentForList.loadList(sharedPrefs)
            notifyItemRemoved(position)
        }

        holder.editItem.setOnClickListener{view ->

            var sharedPrefs =  view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
            val pageContext = view.context.javaClass.toString()
            var layoutName = R.layout.expenses_input_layout
            if (pageContext == "class com.example.expesestracker.ExpensesActivity") {
                layoutName = R.layout.expenses_input_layout
                sharedPrefs = view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
            } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                sharedPrefs =  view.context.getSharedPreferences("test_income", Context.MODE_PRIVATE)
                layoutName = R.layout.incomes_input_layout
            }
            updateItem(view, item , sharedPrefs, layoutName, holder)
            PopulateContentForList.loadList(sharedPrefs)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemType
        val dateView: TextView = binding.itemDate
        val amountView: TextView = binding.itemAmount
        val deleteItem: ImageView = binding.deleteItem
        val editItem: ImageView = binding.editItem

        override fun toString(): String {
            return super.toString() + " '" + dateView.text + "'"
        }
    }
    private fun updateItem(viewIn: View, item: ExpenseItem, sharedPref: SharedPreferences, layoutNM: Int, holder: ViewHolder) {

        val builder = AlertDialog.Builder(viewIn.context)
        val layoutInflater: LayoutInflater = viewIn.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =  layoutInflater.inflate(layoutNM, null)
        val saveButton = view.findViewById<Button>(R.id.save)
        val amount = view.findViewById<EditText>(R.id.amount)
        val itemSpinner = view.findViewById<Spinner>(R.id.itemsSpinner)
        val description = view.findViewById<EditText>(R.id.note)
        saveButton.visibility = INVISIBLE

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            dialog.cancel()
        })

        builder.setPositiveButton("Proceed", DialogInterface.OnClickListener {
                dialog, id ->

        })

        with(builder) {
            setTitle("Edit Item")
            if (item.TYPE.equals("ERROR")){
                Toast.makeText( view.context, "Error Item Does Not Exsist", Toast.LENGTH_SHORT).show();
            }
            else{
                amount.setText(item.AMOUNT.toString())
                for (i in 0 until itemSpinner.count) {
                    if (itemSpinner.getItemAtPosition(i).toString().equals(item.TYPE)) {
                        itemSpinner.setSelection(i)
                        break
                    }
                }
                description.setText(item.DESCRIPTION)
                setView(view)
                setCancelable(true)
            }
            val dialog = builder.create()
            dialog.show()

//            Overriding the Positive button to do validations otherwise it will close the dialog
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                View.OnClickListener {
                    val expenseAmount: String = amount.text.toString()

                    if (TextUtils.isEmpty(expenseAmount)) {
                        amount.error = "Amount is required"
                        return@OnClickListener
                    }
                    else{
                        val expenseAmountFLOAT: Float? = expenseAmount.toFloatOrNull()
                        val expenseItem = itemSpinner.selectedItem.toString()
                        val expenseDescription = description.text.toString()

                        if (expenseItem == "Select Item") {
                            Toast.makeText(
                                view.context,
                                "Select a Type",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@OnClickListener
                        }
                        else{
                            val expense = ExpenseItem(item.ID, expenseItem, item.DATE_TIME , expenseDescription,expenseAmountFLOAT!! )
                            util.updateItem(sharedPref, expense)

//                    val intId = item.ID.filter { it.isDigit() }
//                    val updatedItem: ExpenseItem = util.getItem(sharedPref, intId)
//                    holder.idView.text = updatedItem.TYPE
//                    holder.dateView.text = "updatedItem.DATE_TIME"
//                    holder.amountView.text = updatedItem.AMOUNT.toString()
//                    notifyDataSetChanged()
                            dialog.dismiss()
                        }
                    }

                })
        }
    }
}