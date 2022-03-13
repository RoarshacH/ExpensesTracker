package com.example.expesestracker.models

import java.util.*

data class ExpenseItem(val ID: String, val TYPE: String, val DATE_TIME: String, val DESCRIPTION: String, val AMOUNT: Float ) {
    override fun toString(): String = TYPE
}