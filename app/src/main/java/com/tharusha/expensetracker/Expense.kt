package com.tharusha.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val groupId: Int,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String
)