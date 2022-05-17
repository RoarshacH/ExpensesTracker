package com.example.expesestracker.models

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SQLUtilities(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d/y H:m:ss", Locale.ENGLISH)


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE expenseItems(ID INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount FLOAT ,description TEXT, date Text, category INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS expenseItems")
    }

    fun InsertItem(type: String?, description: String?, date: String?, amount: Float?, category: Int): Boolean {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("type", type)
        contentValues.put("amount", amount)
        contentValues.put("description", description)
        contentValues.put("date", date)
        contentValues.put("category", category)
        val result = sqLiteDatabase.insert("expenseItems", null, contentValues)
        return result != -1L
    }

    fun UpdateItem(item: ExpenseItem): Boolean? {
        val sqLiteDatabase = this.readableDatabase
        val contentValues = ContentValues()
        contentValues.put("type", item.TYPE)
        contentValues.put("description", item.DESCRIPTION)
        contentValues.put("amount", item.AMOUNT)
        val result = sqLiteDatabase.update(
            "expenseItems",
            contentValues,
            "ID" + " = ?",
            arrayOf(java.lang.String.valueOf(item.ID))
        ).toLong()
        return result != -1L
    }

    fun getItem(id: Int) {
        val db = this.readableDatabase
        val cursor = db.query(
            "expenseItems", arrayOf(
                "ID", "title",
                "description", "date"
            ), "ID" + "=?", arrayOf(id.toString()), null, null, null, null
        )
        cursor?.moveToFirst()
        val todo = ExpenseItem(
            cursor!!.getString(0).toString(),
            cursor.getString(1), cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
        )
    }

    fun GetAllItems(category: Int): List<ExpenseItem?>? {
        val toDoList: MutableList<ExpenseItem> = ArrayList<ExpenseItem>()
        // Select All Query
        val selectQuery = "SELECT * FROM expenseItems WHERE category = " + category + " ORDER BY ID DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                toDoList.add(item)
            } while (cursor.moveToNext())
        }
        return toDoList
    }

    fun GetNumOfItems(category: Int): Int {
        // Select All Query
        var count = 0
        val selectQuery = "SELECT COUNT (*) FROM expenseItems WHERE category = " + category
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if(null != cursor)
            if(cursor.count > 0){
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
        cursor.close();
        return count
    }

    fun DeleteItem(toDo: ExpenseItem): Boolean? {
        val db = this.writableDatabase
        val result =
            db.delete("expenseItems", "ID" + " = ?", arrayOf(java.lang.String.valueOf(toDo.ID)))
                .toLong()
        db.close()
        return result != -1L
    }

    fun getLatestItem(category: Int): Float {
        var latest = 0F
        val selectQuery = "SELECT amount FROM expenseItems WHERE category = " + category + " ORDER BY ID DESC LIMIT 1"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if(null != cursor)
            if(cursor.count > 0){
                cursor.moveToLast();
                latest = cursor.getFloat(0);
            }
        cursor.close();
        return latest

    }
    fun getTotalForWeek(category: Int): Float {
        var total = 0F
        val thisWeek = getThisWeekNumber()
        val selectQuery = "SELECT * FROM expenseItems WHERE category = " + category
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                val dateString = item.DATE_TIME
                val originalStartDate = LocalDate.parse(dateString, dateFormatter)
                val dateWeek = LocalDate.of(originalStartDate.year, originalStartDate.month, originalStartDate.dayOfMonth)
                val weekOfYear = dateWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear())
                if (weekOfYear == thisWeek){
                    total += item.AMOUNT
                }
            } while (cursor.moveToNext())
        }
        cursor.close();
        return total
    }

    fun getThisWeekExpenses(): HashMap<String, Float> {
        val hashMap : java.util.HashMap<String, Float>
                = java.util.HashMap()
        var total = 0.0F
        val thisWeek = getThisWeekNumber()
        val selectQuery = "SELECT * FROM expenseItems WHERE category = 1 ORDER BY ID DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                val dateString = item.DATE_TIME
                val originalStartDate = LocalDate.parse(dateString, dateFormatter)
                val dateWeek = LocalDate.of(originalStartDate.year, originalStartDate.month, originalStartDate.dayOfMonth)
                val weekOfYear = dateWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear())

                if (weekOfYear == thisWeek){
                    val getType = item.TYPE
                    val getAmount = item.AMOUNT
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
            } while (cursor.moveToNext())
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this week by type of the items in the DB Expenses
     * @return [HashMap] of items for this week by Type  in the Expenses DB
     */
    fun getThisMonthExpenses(): java.util.HashMap<String, Float> {
        val hashMap : java.util.HashMap<String, Float>
                = java.util.HashMap()
        var total = 0.0F
        val selectQuery = "SELECT * FROM expenseItems WHERE category = 1 ORDER BY ID DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val originalStartDate = LocalDate.parse(dateTime, dateFormatter)
        val thisMonth = originalStartDate.month
        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                val dateString = item.DATE_TIME
                val originalStartDate = LocalDate.parse(dateString, dateFormatter)
                val monthDB = originalStartDate.month

                if (monthDB == thisMonth) {
                    val getType = item.TYPE
                    val getAmount = item.AMOUNT
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
            } while (cursor.moveToNext())
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this month by type of the items in the DB Incomes
     * @return [HashMap] of items for this month by Type  in the Incomes DB
     */
    fun getThisMonthIncomes(): java.util.HashMap<String, Float> {
        val hashMap : java.util.HashMap<String, Float>
                = java.util.HashMap()
        var total = 0.0F
        val selectQuery = "SELECT * FROM expenseItems WHERE category = 0 ORDER BY ID DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val originalStartDate = LocalDate.parse(dateTime, dateFormatter)
        val thisMonth = originalStartDate.month

        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                val dateString = item.DATE_TIME
                val originalStartDate = LocalDate.parse(dateString, dateFormatter)
                val monthDB = originalStartDate.month

                if (monthDB == thisMonth) {
                    val getType = item.TYPE
                    val getAmount = item.AMOUNT
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
            } while (cursor.moveToNext())
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    /**
     * @param sharedPreferences Shared Preferences reference
     * Get the result only of items added this week by type of the items in the DB Incomes
     * @return [HashMap] of items for this week by Type  in the Incomes DB
     */
    fun getThisWeekIncomes(): java.util.HashMap<String, Float> {
        val hashMap : java.util.HashMap<String, Float>
                = java.util.HashMap()
        var total = 0.0F
        val thisWeek = getThisWeekNumber()
        val selectQuery = "SELECT * FROM expenseItems WHERE category = 0 ORDER BY ID DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val item = ExpenseItem(cursor!!.getString(0).toString(), cursor.getString(1),
                    cursor.getString(4), cursor.getString(3), cursor.getString(2).toFloat()
                )
                val dateString = item.DATE_TIME
                val originalStartDate = LocalDate.parse(dateString, dateFormatter)
                val dateWeek = LocalDate.of(originalStartDate.year, originalStartDate.month, originalStartDate.dayOfMonth)
                val weekOfYear = dateWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear())

                if (weekOfYear == thisWeek){
                    val getType = item.TYPE
                    val getAmount = item.AMOUNT
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
            } while (cursor.moveToNext())
        }
        putValueInHashMap(hashMap, "Total", total)
        return hashMap
    }

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "expensesTracker"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        val EXPENSE_TYPE= 1
        val INCOME_TYPE = 0
    }
    private fun getThisWeekNumber(): Int {
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
        val dateFormatter = DateTimeFormatter.ofPattern("M/d/y H:m:ss", Locale.ENGLISH)
        val originalStartDate = LocalDate.parse(dateTime, dateFormatter)
        val dateWeek = LocalDate.of(originalStartDate.year, originalStartDate.month, originalStartDate.dayOfMonth)
        Log.i("DATE", "NOW" + dateWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear()).toString())

        return  dateWeek.get(WeekFields.of(Locale.ENGLISH).weekOfYear())
    }

    /**
     * @param hashmap hashMap
     * @param type key Value
     * @param amount Value
     * put the given key and value in the given hashmap
     */
    private fun putValueInHashMap(hashmap: java.util.HashMap<String, Float>, type: String, amount: Float) {
        var temp = 0.0F
        if(hashmap.containsKey(type)){
            temp = hashmap[type]!!
        }
        temp = temp.plus(amount)
        hashmap[type] = temp
    }

}