package com.tharusha.expensetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Mark the class as a Room Database and specify the tables (Entities)
// මෙහි entities array එකට ExpenseGroup::class අලුතින් එකතු කර ඇත
@Database(entities = [Expense::class, ExpenseGroup::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Link the DAO to this database
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Using Singleton pattern to ensure only one instance of the database is created
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}