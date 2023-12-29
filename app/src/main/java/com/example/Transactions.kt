package com.example
import androidx.recyclerview.widget.DiffUtil
import java.sql.Timestamp

data class Transactions(
    val ExpenseType:String,
    val amount: Number,
    val date:String,
    val description : String,
    val label : String,
    val timestampID : String
)

class ExpenseDiffCallBack : DiffUtil.ItemCallback<Transactions>() {
    override fun areItemsTheSame(oldItem: Transactions, newItem: Transactions): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Transactions, newItem: Transactions): Boolean {
        return oldItem == newItem
    }
}
