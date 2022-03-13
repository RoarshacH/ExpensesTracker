package com.example.expesestracker.models

import android.content.SharedPreferences
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class DBUtilities {

    fun saveItem(sharedPreferences: SharedPreferences, dbName:String, item: ExpenseItem){
        //Get Number of items rough way of keeping the id

        var numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        var id = sharedPreferences.getInt(Constants.ID, 0)
        numberOfItems++
        id++
        Log.i("Saved", numberOfItems.toString())
        //Using the sharedPreferences object do the ones below // Save/Update the value
        with(sharedPreferences.edit()){
            putInt(Constants.NUMBEROFITEMS, numberOfItems)
            putInt(Constants.ID, id)

            putString(Constants.TYPE + id, item.TYPE)

            val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
            putString(Constants.DATE_TIME + id, dateTime)
            putFloat(Constants.AMOUNT + id, item.AMOUNT)
            putString(Constants.DESCRIPTION + id, item.DESCRIPTION)

            apply() //this is async commit() is sync both can be used but better not to call commit()
        }

////        For Testing
//        val temo = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
//        Log.i("Saved", temo.toString())
//        val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +numberOfItems, 0.0F)
//        Log.i("Saved", getAmount.toString())
    }

    fun deleteItem(sharedPreferences: SharedPreferences, dbName:String, item: ExpenseItem){
        var numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        var id = item.ID
        var result = id.filter { it.isDigit() }
        Log.i("Saved", numberOfItems.toString())
        //Using the sharedPreferences object do the ones below // Save/Update the value
        with(sharedPreferences.edit()){
            remove(Constants.TYPE + result)
            remove(Constants.DATE_TIME + result)
            remove(Constants.AMOUNT + result)
            remove(Constants.DESCRIPTION + result)
            numberOfItems--
            putInt(Constants.NUMBEROFITEMS, numberOfItems)
            apply() //this is async commit() is sync both can be used but better not to call commit()
        }
        numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        Log.i("Saved", "NEW  " + numberOfItems)
        val getType = sharedPreferences.getString(Constants.TYPE + result, "ERROR")
        Log.i("Saved", getType.toString())

    }

    fun getValues(sharedPreferences: SharedPreferences): MutableList<ExpenseItem> {
        val expensesList: MutableList<ExpenseItem> = ArrayList()
        var id = sharedPreferences.getInt(Constants.ID, 0)
        for (i in 1..id) {
//            Add a if to pass if there is empty
            val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
            val getDateTime = sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)
            val getDescription = sharedPreferences.getString(Constants.DESCRIPTION +i, "")

            Log.i("TEST", getType.toString() + " " + getAmount.toString() )

            if (getType.equals("ERROR")){
                //Do nothing
            }
            else{
                val pastGame =  ExpenseItem("Item " + i.toString(), getType!!, getDateTime!!, getDescription!!, getAmount!!)
                expensesList.add(pastGame)
            }
        }
        Log.i("TEST", "--------------------------------------------------------------" )
        return expensesList
    }

    fun getNumberOfItems(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)

    }

    fun getTotal(sharedPreferences: SharedPreferences): Float {
        val id = sharedPreferences.getInt(Constants.ID, 0)
        var total:Float = 0.0F
        for (i in 1..id) {
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)
            total += getAmount
        }
        return total
    }

    fun getLatest(sharedPreferences: SharedPreferences): Float {
        var list: MutableList<ExpenseItem> = getValues(sharedPreferences)
        list.reverse()
        var item: ExpenseItem = list[0]
        return item.AMOUNT
    }

    fun getTotalByTypeIncomes(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
        var id = sharedPreferences.getInt(Constants.ID, 0)
        for (i in 1..id) {
            val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)

            when(getType){
                Constants.SALARY -> putValueInHashMap(hashMap, Constants.SALARY, getAmount)
                Constants.Savings ->  putValueInHashMap(hashMap, Constants.Savings, getAmount)
                Constants.INTEREST -> putValueInHashMap(hashMap, Constants.INTEREST, getAmount)
                Constants.INVASMENTS -> putValueInHashMap(hashMap, Constants.INVASMENTS, getAmount)
                Constants.SOCIALBENIFITS -> putValueInHashMap(hashMap, Constants.SOCIALBENIFITS, getAmount)
                Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
            }

        }
        return hashMap
    }

    fun getTotalByTypeExpenses(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
//        val numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        var id = sharedPreferences.getInt(Constants.ID, 0)
        for (i in 1..id) {
            val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)

            when(getType){
                Constants.TRANSPORT -> putValueInHashMap(hashMap, Constants.TRANSPORT, getAmount)
                Constants.GROCERY ->  putValueInHashMap(hashMap, Constants.GROCERY, getAmount)
                Constants.UTILITIES -> putValueInHashMap(hashMap, Constants.UTILITIES, getAmount)
                Constants.ENTERTAINMENT -> putValueInHashMap(hashMap, Constants.ENTERTAINMENT, getAmount)
                Constants.CLOTHES -> putValueInHashMap(hashMap, Constants.CLOTHES, getAmount)
                Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
            }

        }
        return hashMap
    }

    private fun putValueInHashMap(hashmap: HashMap<String, Float>, type: String, amount: Float) {
        var temp = 0.0F
        if(hashmap.containsKey(type)){
            temp = hashmap[type]!!
        }
        temp = temp.plus(amount)
        hashmap[type] = temp
    }
}

class Constants{
    companion object{
        var ID = "ID"
        var NUMBEROFITEMS = "NUMBEROFITEMS"
        var TYPE = "TYPE"
        var AMOUNT = "AMOUNT"
        val DATE_TIME = "DATE_TIME"
        val DESCRIPTION = "DESCRIPTION"

        val TRANSPORT= "Transport"
        val GROCERY = "Grocery"
        val UTILITIES = "Utilities"
        val ENTERTAINMENT = "Entertainment"
        val CLOTHES = "Clothes"
        val OTHER = "Other"

        val SALARY = "Salary"
        val Savings = "Savings"
        val INTEREST = "Interest"
        val SOCIALBENIFITS = "Social Benefits"
        val INVASMENTS = "Investments"

    }
}
