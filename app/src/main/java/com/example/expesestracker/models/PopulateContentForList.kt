package com.example.expesestracker.models

import android.content.SharedPreferences
import android.util.Log
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 */
object PopulateContentForList {
    /**
     * An array of sample (placeholder) items.
     */
    var ITEMS: MutableList<ExpenseItem> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    var ITEM_MAP: MutableMap<String, ExpenseItem> = HashMap()

    private var COUNT = 0

    fun loadList(util: SQLUtilities, category:Int){

        COUNT = util.GetNumOfItems(category)
        val expensesList: MutableList<ExpenseItem> = util.GetAllItems(category) as MutableList<ExpenseItem>
        ITEMS = ArrayList()
        ITEM_MAP = HashMap()
        expensesList.forEach { expense:ExpenseItem ->
            val expenseItem =  ExpenseItem(expense.ID, expense.TYPE, expense.DATE_TIME, expense.DESCRIPTION, expense.AMOUNT)
            Log.i("Count", expenseItem.toString())
            addItem(expenseItem)
        }
    }
    private fun addItem(item: ExpenseItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.ID, item)
    }
}