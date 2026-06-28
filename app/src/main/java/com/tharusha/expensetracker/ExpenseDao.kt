package com.tharusha.expensetracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Insert
    suspend fun insertGroup(group: ExpenseGroup)

    @Query("SELECT * FROM group_table")
    suspend fun getAllGroups(): List<ExpenseGroup>

    @Query("SELECT * FROM expense_table")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT COUNT(*) FROM expense_table WHERE groupId = :groupId")
    suspend fun getExpenseCountByGroup(groupId: Int): Int

    // --- Cloud Restore Methods ---
    @Query("DELETE FROM expense_table")
    suspend fun deleteAllExpenses()

    @Query("DELETE FROM group_table")
    suspend fun deleteAllGroups()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGroups(groups: List<ExpenseGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(expenses: List<Expense>)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expense_table WHERE groupId = :groupId")
    suspend fun getExpensesByGroup(groupId: Int): List<Expense>

    @Query("SELECT * FROM expense_table WHERE groupId = :groupId ORDER BY date DESC")
    suspend fun getExpensesSortedByDate(groupId: Int): List<Expense>

    @Query("SELECT * FROM expense_table WHERE groupId = :groupId ORDER BY amount DESC")
    suspend fun getExpensesSortedByAmount(groupId: Int): List<Expense>

    @Query("SELECT * FROM expense_table WHERE groupId = :groupId AND (title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%')")
    suspend fun searchExpensesInGroup(groupId: Int, query: String): List<Expense>
}