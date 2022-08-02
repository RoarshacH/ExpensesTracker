package com.example.expesestracker

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.expesestracker.databinding.FragmentItemBinding
import com.example.expesestracker.models.ExpenseItem
import com.example.expesestracker.models.PopulateContentForList
import com.example.expesestracker.models.SQLUtilities
import java.text.SimpleDateFormat
import java.util.*


/**
 * [RecyclerView.Adapter] that can display a [ExpenseItem].
 */
class MyDataItemRecyclerViewAdapter(
    private var values: List<ExpenseItem>
) : RecyclerView.Adapter<MyDataItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.TYPE
        val date = SimpleDateFormat("M/d/y H:m:ss").parse(item.DATE_TIME) as Date
        val formatter = SimpleDateFormat("dd-MMM HH:mm ")
        val dateTime = formatter.format(date)
        holder.dateView.text = dateTime.toString()
        holder.amountView.text = item.AMOUNT.toString()

        holder.deleteItem.setOnClickListener { view ->
            val pageContext = view.context.javaClass.toString()
            var category = 0
            if (pageContext ==  "class com.example.expesestracker.ExpensesActivity") {
                category = 1
            } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                category = 0
            }
            deleteItem(view, item, category, position)
        }

        holder.editItem.setOnClickListener{view ->
            val pageContext = view.context.javaClass.toString()
            var layoutName = R.layout.expenses_input_layout
            var category = 0
            if (pageContext == "class com.example.expesestracker.ExpensesActivity") {
                layoutName = R.layout.expenses_input_layout
                category = 1
            } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                layoutName = R.layout.incomes_input_layout
                category = 0
            }
            updateItem(view, item, layoutName, category)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemType
        val dateView: TextView = binding.itemDate
        val amountView: TextView = binding.itemAmount
        val deleteItem: ImageView = binding.deleteItem
        val editItem: LinearLayout = binding.layoutItemDetails

        override fun toString(): String {
            return super.toString() + " '" + dateView.text + "'"
        }
    }

    private fun deleteItem(viewIn: View, item: ExpenseItem, category: Int, position: Int){
        val builder = AlertDialog.Builder(viewIn.context)
        val dbUtilities = SQLUtilities(viewIn.context)
        builder.setMessage(R.string.dlt_message)
            .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ ->
                // START THE GAME!
                val result = dbUtilities.DeleteItem(item)
                if (result == true) {
                    PopulateContentForList.loadList(dbUtilities, category)
                    val newList = PopulateContentForList.ITEMS
                    updateItemsList(newList)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(viewIn.context, "Error Deleting Item", Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton(R.string.no, DialogInterface.OnClickListener { dialog, _ ->
                // User cancelled the dialog
                dialog.cancel()
            })
        val dialog = builder.create()
        dialog.show()
    }
    private fun updateItem(
        viewIn: View,
        item: ExpenseItem,
        layoutNM: Int,
        category:Int
    ) {

        val builder = AlertDialog.Builder(viewIn.context)
        val layoutInflater: LayoutInflater = viewIn.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =  layoutInflater.inflate(layoutNM, null)
        val saveButton = view.findViewById<Button>(R.id.save)
        val amount = view.findViewById<EditText>(R.id.amount)
        val itemSpinner = view.findViewById<Spinner>(R.id.itemsSpinner)
        val description = view.findViewById<EditText>(R.id.note)
        saveButton.visibility = INVISIBLE

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        builder.setPositiveButton("Proceed", DialogInterface.OnClickListener {
                _, _ ->

        })

        with(builder) {
            setTitle("Edit Item")
            if (item.TYPE.equals("ERROR")){
                Toast.makeText( view.context, "Error Item Does Not Exist", Toast.LENGTH_SHORT).show()
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
                            val dbUtilities = SQLUtilities(view.context)
                            val result = dbUtilities.UpdateItem(expense)
                            if (result == true) {
                                PopulateContentForList.loadList(dbUtilities, category)
                                val newList = PopulateContentForList.ITEMS
                                updateItemsList(newList)
                            } else {
                                Toast.makeText(view.context, "Error Deleting Item", Toast.LENGTH_SHORT).show()
                            }
                            dialog.dismiss()
                        }
                    }
                })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemsList(newList: List<ExpenseItem>) {
        values = newList
        notifyDataSetChanged()
    }
}