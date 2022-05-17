package com.example.expesestracker.models

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLUtilities(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


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

    companion object{
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "expensesTracker"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "gfg_table"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COl = "name"

        // below is the variable for age column
        val AGE_COL = "age"

        val EXPENSE_TYPE= 1
        val INCOME_TYPE = 0
    }
}