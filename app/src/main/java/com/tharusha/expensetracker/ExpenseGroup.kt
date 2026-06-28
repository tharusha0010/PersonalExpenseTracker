package com.tharusha.expensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_table")
data class ExpenseGroup(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    val groupName: String
)