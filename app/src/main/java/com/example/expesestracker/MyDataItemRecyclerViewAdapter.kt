package com.example.expesestracker

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.example.expesestracker.databinding.FragmentItemBinding
import com.example.expesestracker.models.DBUtilities
import com.example.expesestracker.models.ExpenseItem


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
            val pageContext = view.getContext().javaClass.toString()

            if (pageContext == "class com.example.expesestracker.ExpensesActivity") {
                sharedPrefs = view.context.getSharedPreferences("test-expenses", Context.MODE_PRIVATE)
                Log.i("SAVE", "INSODE EXPENSE")
            } else if (pageContext == "class com.example.expesestracker.IncomeActivity") {
                sharedPrefs =  view.context.getSharedPreferences("test_income", Context.MODE_PRIVATE)
                Log.i("SAVE", "INSIDE INCOME")
            }
            util.deleteItem(sharedPrefs, "EXPENSE", item)
            notifyDataSetChanged()
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

}