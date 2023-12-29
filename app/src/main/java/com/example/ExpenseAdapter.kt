package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupauth.R

class ExpenseAdapter(private val onItemClickListener: (Transactions) -> Unit) :
    ListAdapter<Transactions, ExpenseAdapter.ViewHolder>(ExpenseDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)

        // Set an item click listener
        holder.itemView.setOnClickListener {
            val clickedExpense = getItem(position)
            onItemClickListener.invoke(clickedExpense)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expenseType = itemView.findViewById<TextView>(R.id.inc_or_exp)
        private val label = itemView.findViewById<TextView>(R.id.labelT)
        private val amount = itemView.findViewById<TextView>(R.id.amountT)
        private val date = itemView.findViewById<TextView>(R.id.dateT)

        fun bind(expense: Transactions) {
            label.text = "${expense.label}"
            expenseType.text = "${expense.ExpenseType}"
            amount.text = "${expense.amount}"
            date.text = "${expense.date}"
        }
    }
}
