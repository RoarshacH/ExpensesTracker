package com.example.expesestracker.models

import android.content.SharedPreferences
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class DBUtilities {


    /**
     * @param sharedPreferences Shared Preferences reference to add
     * @param item item to Add
     * Adds a [ExpenseItem] to the DB. and increase the number of items parameter
     */
    fun saveItem(sharedPreferences: SharedPreferences, dbName:String, item: ExpenseItem){
        //Get Number of items rough way of keeping the id
        var numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        var id = sharedPreferences.getInt(Constants.ID, 0)
        numberOfItems++
        id++
        Log.i("Saved", numberOfItems.toString())
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

    /**
     * Removes the All shared preferences Storage. Do not use unless want to clear storage
     * @return nothing
     */
    fun deleteALL(sharedPreferences: SharedPreferences){
        with(sharedPreferences.edit()){
            clear();
            commit(); // commit changes
        }

    }

    /**
     * @param sharedPreferences Shared Preferences reference to add
     * @param item item to delete
     * Removes a [ExpenseItem] from DB.
     */
    fun deleteItem(sharedPreferences: SharedPreferences, dbName:String, item: ExpenseItem){

        var numberOfItems = sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
        var id = item.ID
        var result = id.filter { it.isDigit() }
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
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Gets all items in the relevant DB
     * @return [MutableList] of items in the database
     */
    fun getValues(sharedPreferences: SharedPreferences): MutableList<ExpenseItem> {
        val expensesList: MutableList<ExpenseItem> = ArrayList()
        var id = sharedPreferences.getInt(Constants.ID, 0)
        for (i in 1..id) {
            val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
            val getDateTime = sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)
            val getDescription = sharedPreferences.getString(Constants.DESCRIPTION +i, "")

            if (getType.equals("ERROR")){
                //Do nothing
            }
            else{
                val pastGame =  ExpenseItem("Item " + i.toString(), getType!!, getDateTime!!, getDescription!!, getAmount!!)
                expensesList.add(pastGame)
            }
        }
        return expensesList
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the number of items in the DB
     * @return [Int] of number of items in the database
     */
    fun getNumberOfItems(sharedPreferences: SharedPreferences): Int {
        return sharedPreferences.getInt(Constants.NUMBEROFITEMS, 0)
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the total of items in the given DB
     * @return [Float] of the total of items in the database
     */
    fun getTotal(sharedPreferences: SharedPreferences): Float {
        val id = sharedPreferences.getInt(Constants.ID, 0)
        var total:Float = 0.0F
        for (i in 1..id) {
            val getAmount = sharedPreferences.getFloat(Constants.AMOUNT +i, 0.0F)
            total += getAmount
        }
        return total
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the latest added value of the item in the given DB
     * @return [Float] value of latest item
     */
    fun getLatest(sharedPreferences: SharedPreferences): Float {
        var list: MutableList<ExpenseItem> = getValues(sharedPreferences)
        if (list.count() != 0){
            list.reverse()

            var item: ExpenseItem = list[0]
            return item.AMOUNT
        }
        else{
            return 0.0F
        }
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result by type of the items in the DB Incomes
     * @return [HashMap] of items by Type in the Incomes DB
     */
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

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result by type of the items in the DB Expenses
     * @return [HashMap] of items by Type in the Expenses DB
     */
    fun getTotalByTypeExpenses(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
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

    /**
     * @param hashmap hashMap
     * @param type key Value
     * @param amount Value
     * put the given key and value in the given hashmap
     */
    private fun putValueInHashMap(hashmap: HashMap<String, Float>, type: String, amount: Float) {
        var temp = 0.0F
        if(hashmap.containsKey(type)){
            temp = hashmap[type]!!
        }
        temp = temp.plus(amount)
        hashmap[type] = temp
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this week by type of the items in the DB Expenses
     * @return [HashMap] of items for this week by Type  in the Expenses DB
     */
    fun getThisWeekExpenses(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
        var total:Float = 0.0F
        var id = sharedPreferences.getInt(Constants.ID, 0)
        val thisWeek = getThisWeekNumber()

        for (i in 1..id) {

            var dateString =sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")

            if (dateString.equals("ERROR")){
//                Do Nothing
            }
            else{
                val date = SimpleDateFormat("M/d/y H:m:ss").parse(dateString)
                val dateDB: LocalDate = LocalDate.of(date.year, date.month, date.date)
                val weekOfYear: Int = dateDB.get(WeekFields.of(Locale.ENGLISH).weekOfYear())

                if (weekOfYear == thisWeek){
                    val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
                    val getAmount = sharedPreferences.getFloat(Constants.AMOUNT + i, 0.0F)
                    total += getAmount
                    when(getType){
                        Constants.TRANSPORT -> putValueInHashMap(hashMap, Constants.TRANSPORT, getAmount)
                        Constants.GROCERY ->  putValueInHashMap(hashMap, Constants.GROCERY, getAmount)
                        Constants.UTILITIES -> putValueInHashMap(hashMap, Constants.UTILITIES, getAmount)
                        Constants.ENTERTAINMENT -> putValueInHashMap(hashMap, Constants.ENTERTAINMENT, getAmount)
                        Constants.CLOTHES -> putValueInHashMap(hashMap, Constants.CLOTHES, getAmount)
                        Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
                    }
                }
            }
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this month by type of the items in the DB Expenses
     * @return [HashMap] of items for this month by Type  in the Expenses DB
     */
    fun getThisMonthExpenses(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
        var total:Float = 0.0F
        var id = sharedPreferences.getInt(Constants.ID, 0)
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val dateNow = SimpleDateFormat("M/d/y H:m:ss").parse(dateTime)
        val thisMonth = dateNow.month

        for (i in 1..id) {

            var dateString =sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")
            if (dateString.equals("ERROR")){
//                Do Nothing
            }
            else{
                val date = SimpleDateFormat("M/d/y H:m:ss").parse(dateString)
                val monthDB =  date.month

                if (monthDB == thisMonth){
                    val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
                    val getAmount = sharedPreferences.getFloat(Constants.AMOUNT + i, 0.0F)
                    total += getAmount
                    when(getType){
                        Constants.TRANSPORT -> putValueInHashMap(hashMap, Constants.TRANSPORT, getAmount)
                        Constants.GROCERY ->  putValueInHashMap(hashMap, Constants.GROCERY, getAmount)
                        Constants.UTILITIES -> putValueInHashMap(hashMap, Constants.UTILITIES, getAmount)
                        Constants.ENTERTAINMENT -> putValueInHashMap(hashMap, Constants.ENTERTAINMENT, getAmount)
                        Constants.CLOTHES -> putValueInHashMap(hashMap, Constants.CLOTHES, getAmount)
                        Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
                    }
                }
            }
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this month by type of the items in the DB Incomes
     * @return [HashMap] of items for this month by Type  in the Incomes DB
     */
    fun getThisMonthIncomes(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
        var total:Float = 0.0F
        var id = sharedPreferences.getInt(Constants.ID, 0)
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val dateNow = SimpleDateFormat("M/d/y H:m:ss").parse(dateTime)
        val thisMonth = dateNow.month

        for (i in 1..id) {

            var dateString =sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")
            if (dateString.equals("ERROR")){
//                Do Nothing
            }
            else {
                val date = SimpleDateFormat("M/d/y H:m:ss").parse(dateString)
                val monthDB = date.month

                if (monthDB == thisMonth) {
                    val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
                    val getAmount = sharedPreferences.getFloat(Constants.AMOUNT + i, 0.0F)
                    total += getAmount
                    when (getType) {
                        Constants.SALARY -> putValueInHashMap(hashMap, Constants.SALARY, getAmount)
                        Constants.Savings -> putValueInHashMap(
                            hashMap,
                            Constants.Savings,
                            getAmount
                        )
                        Constants.INTEREST -> putValueInHashMap(
                            hashMap,
                            Constants.INTEREST,
                            getAmount
                        )
                        Constants.INVASMENTS -> putValueInHashMap(
                            hashMap,
                            Constants.INVASMENTS,
                            getAmount
                        )
                        Constants.SOCIALBENIFITS -> putValueInHashMap(
                            hashMap,
                            Constants.SOCIALBENIFITS,
                            getAmount
                        )
                        Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
                    }
                }
            }
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this week by type of the items in the DB Incomes
     * @return [HashMap] of items for this week by Type  in the Incomes DB
     */
    fun getThisWeekIncomes(sharedPreferences: SharedPreferences): HashMap<String, Float> {
        var hashMap : HashMap<String, Float>
                = HashMap ()
        var total:Float = 0.0F
        var id = sharedPreferences.getInt(Constants.ID, 0)
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val dateNow = SimpleDateFormat("M/d/y H:m:ss").parse(dateTime)
        val thisMonth = dateNow.month

        for (i in 1..id) {

            var dateString = sharedPreferences.getString(Constants.DATE_TIME + i, "ERROR")
            if (dateString.equals("ERROR")) {
//                Do Nothing
            } else {
                val date = SimpleDateFormat("M/d/y H:m:ss").parse(dateString)
                val monthDB = date.month

                if (monthDB == thisMonth) {
                    val getType = sharedPreferences.getString(Constants.TYPE + i, "ERROR")
                    val getAmount = sharedPreferences.getFloat(Constants.AMOUNT + i, 0.0F)
                    total += getAmount
                    when (getType) {
                        Constants.SALARY -> putValueInHashMap(hashMap, Constants.SALARY, getAmount)
                        Constants.Savings -> putValueInHashMap(
                            hashMap,
                            Constants.Savings,
                            getAmount
                        )
                        Constants.INTEREST -> putValueInHashMap(
                            hashMap,
                            Constants.INTEREST,
                            getAmount
                        )
                        Constants.INVASMENTS -> putValueInHashMap(
                            hashMap,
                            Constants.INVASMENTS,
                            getAmount
                        )
                        Constants.SOCIALBENIFITS -> putValueInHashMap(
                            hashMap,
                            Constants.SOCIALBENIFITS,
                            getAmount
                        )
                        Constants.OTHER -> putValueInHashMap(hashMap, Constants.OTHER, getAmount)
                    }

                }
            }
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * Get the week number by year
     * @return [Int] of the week number
     */
    private fun getThisWeekNumber(): Int {
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val dateNow = SimpleDateFormat("M/d/y H:m:ss").parse(dateTime)
        val dateDBWeek: LocalDate = LocalDate.of(dateNow.year, dateNow.month, dateNow.date)
        return dateDBWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear())
    }
}

/**
 * Constants used in the App
 */
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
        val SOCIALBENIFITS = "Benefits"
        val INVASMENTS = "Investments"

    }
}
