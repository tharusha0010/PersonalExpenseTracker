package com.tharusha.expensetracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView // අලුතින් එකතු කළ import එක
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var adapter: ExpenseAdapter
    private lateinit var db: AppDatabase
    private var selectedExpense: Expense? = null
    private var currentGroupId: Int = -1
    private var currentGroupName: String = "" // Group නම සේව් කරගන්න

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        db = AppDatabase.getDatabase(this)

        // MainActivity එකෙන් එවන Group ID එකයි Group නමයි ලබාගැනීම
        currentGroupId = intent.getIntExtra("GROUP_ID", 1)
        currentGroupName = intent.getStringExtra("GROUP_NAME") ?: "Expense Details"

        // Initialize UI Components
        val btnBack = findViewById<Button>(R.id.btnBack)
        val tvGroupNameHeading = findViewById<TextView>(R.id.tvGroupNameHeading) // අලුත් Heading එක
        val etTitle = findViewById<TextInputEditText>(R.id.etTitle)
        val etAmount = findViewById<TextInputEditText>(R.id.etAmount)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etCategory = findViewById<TextInputEditText>(R.id.etCategory)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val btnSortDate = findViewById<Button>(R.id.btnSortDate)
        val btnSortAmount = findViewById<Button>(R.id.btnSortAmount)
        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)

        // Heading එකට Group එකේ නම සෙට් කිරීම
        tvGroupNameHeading.text = currentGroupName

        // Navigate back when the Back arrow is clicked
        btnBack.setOnClickListener { finish() }

        adapter = ExpenseAdapter(
            expenseList = emptyList(),
            onDeleteClicked = { expense -> deleteExpense(expense) },
            onEditClicked = { expense ->
                // Populate fields when Edit icon is clicked
                selectedExpense = expense
                etTitle.setText(expense.title)
                etAmount.setText(expense.amount.toString())
                etDate.setText(expense.date)
                etCategory.setText(expense.category)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load data initially
        loadExpensesOnly()


        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                performSearch(query)
            }
        })


        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            val date = etDate.text.toString()
            val category = etCategory.text.toString()

            if (title.isNotEmpty() && amount > 0) {
                lifecycleScope.launch(Dispatchers.IO) {

                    if (selectedExpense == null) {
                        // Insert Mode: Create a new expense
                        db.expenseDao().insertExpense(
                            Expense(groupId = currentGroupId, title = title, amount = amount, date = date, category = category)
                        )
                    } else {
                        // Update Mode: Edit an existing expense
                        val updated = selectedExpense!!.copy(
                            title = title, amount = amount, date = date, category = category
                        )
                        db.expenseDao().updateExpense(updated)
                        selectedExpense = null // Reset selection after update
                    }

                    loadExpensesOnly() // Refresh list

                    withContext(Dispatchers.Main) {
                        // Clear the input fields
                        etTitle.text?.clear()
                        etAmount.text?.clear()
                        etDate.text?.clear()
                        etCategory.text?.clear()

                        Toast.makeText(this@ExpenseListActivity, "Saved Successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill Title and Amount", Toast.LENGTH_SHORT).show()
            }
        }

        btnSortDate.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val sortedList = db.expenseDao().getExpensesSortedByDate(currentGroupId)
                withContext(Dispatchers.Main) { adapter.updateList(sortedList) }
            }
        }

        btnSortAmount.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val sortedList = db.expenseDao().getExpensesSortedByAmount(currentGroupId)
                withContext(Dispatchers.Main) { adapter.updateList(sortedList) }
            }
        }
    }

    private fun loadExpensesOnly() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = db.expenseDao().getExpensesByGroup(currentGroupId)
            withContext(Dispatchers.Main) { adapter.updateList(list) }
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val resultList = if (query.isEmpty()) {
                db.expenseDao().getExpensesByGroup(currentGroupId)
            } else {
                db.expenseDao().searchExpensesInGroup(currentGroupId, query)
            }

            withContext(Dispatchers.Main) {
                adapter.updateList(resultList)
            }
        }
    }

    private fun deleteExpense(expense: Expense) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.expenseDao().deleteExpense(expense)
            loadExpensesOnly()
        }
    }
}