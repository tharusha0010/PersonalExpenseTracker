package com.tharusha.expensetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var expenseList: List<Expense>,
    private val onDeleteClicked: (Expense) -> Unit,
    private val onEditClicked: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        // අලුතින් එකතු කළ Date එක
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = expenseList[position]
        holder.tvTitle.text = currentExpense.title
        holder.tvAmount.text = "Rs. ${currentExpense.amount}"
        holder.tvCategory.text = currentExpense.category
        // Date එක UI එකට සෙට් කිරීම
        holder.tvDate.text = currentExpense.date

        holder.btnEdit.setOnClickListener {
            onEditClicked(currentExpense)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClicked(currentExpense)
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateList(newList: List<Expense>) {
        expenseList = newList
        notifyDataSetChanged()
    }
}