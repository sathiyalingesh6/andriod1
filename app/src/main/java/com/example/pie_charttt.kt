//package com.example
//
//import android.graphics.Color
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.loginsignupauth.R
//import ir.mahozad.android.PieChart
//
//class pie_charttt : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.pie_chart)
//
//        val transactionList = intent.getSerializableExtra("transactionList") as ArrayList<Transactions>?
//
//        if (transactionList != null) {
//            val totalExpense = calculateTotalExpense(transactionList)
//            Toast.makeText(this, "total exp : ${totalExpense}", Toast.LENGTH_SHORT).show()
//
//            val totalIncome = getTotalIncome(transactionList)
//            displayExpenseAsPieChart(totalExpense, totalIncome)
//        }
//    }
//
//    private fun calculateTotalExpense(transactionList: ArrayList<Transactions>): Double {
//        var totalExpense = 0.0
//
//        for (transaction in transactionList) {
//            if (transaction.ExpenseType == "Expense") {
//                totalExpense += transaction.amount.toDouble()
//            }
//        }
//
//        return totalExpense
//    }
//
//    private fun displayExpenseAsPieChart(totalExpense: Double,totalIncome : Double) {
//        val pieChart = findViewById<PieChart>(R.id.pieChart)
//
//        // Calculate the percentage of total expense
//        val percentage = totalExpense / totalIncome * 100
//
//        // Create a slice for the total expense
//        val expenseSlice = PieChart.Slice(percentage.toFloat(), Color.RED)
//
//        // Create a slice for the remaining income
//        val incomeSlice = PieChart.Slice((100 - percentage).toFloat(), Color.GREEN)
//
//        // Set the slices for the pie chart
//        pieChart.slices = listOf(expenseSlice, incomeSlice)
//    }
//
//    private fun getTotalIncome(transactionList: ArrayList<Transactions>): Double {
//        var totalIncome = 0.0
//
//        for (transaction in transactionList) {
//            if (transaction.ExpenseType == "Income") {
//                totalIncome += transaction.amount.toDouble()
//            }
//        }
//
//        return totalIncome
//    }
//}

package com.example

import android.app.slice.Slice
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupauth.R
import com.example.loginsignupauth.databinding.PieChartBinding
import ir.mahozad.android.PieChart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class pie_charttt : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pie_chart)

        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid.toString()
        var transactions = ArrayList<Transactions>()
        //Toast.makeText(this, "user : ${userId}", Toast.LENGTH_SHORT).show()

        if (userId != null) {
            // Reference to Firestore
            val firestoredb = FirebaseFirestore.getInstance()
            val firebaseauth = FirebaseAuth.getInstance()
            val userId = firebaseauth.currentUser?.uid.toString()
            //to fetch userid.user table
            val user = firestoredb.collection("user").document(userId)
            //toget into expense
            val expensecollec = user.collection("expenses")


            //to fetch data into array list
            expensecollec.get().addOnSuccessListener { documentSnapshot ->


                    for (userdata in documentSnapshot.documents) {
                        //val user = userdata.toObject(Transactions::class.java)
                        val expenseType = userdata.getString("ExpenseType")
                        val amount = userdata.get("amount")
                        val label = userdata.getString("label")
                        val description = userdata.getString("description")
                        val date = userdata.getString("date")
                        val timeStampID = userdata.getString("timeStampID")
                        val trans = Transactions(
                            expenseType.toString(),
                            amount as Number, date.toString(), description.toString(), label.toString(), timeStampID.toString()
                        )
                        transactions.add(trans)
                    }

                    // Calculate and display the pie chart
                    val totalExpense = calculateTotalExpense(transactions)
                    val totalIncome = calculateTotalIncome(transactions)
                        //Toast.makeText(this, "total exp : $totalExpense", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, "total inc : $totalIncome", Toast.LENGTH_SHORT).show()


                displayExpenseAsPieChart(totalExpense,totalIncome)
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                }
        }
    }

    private fun calculateTotalExpense(transactions: List<Transactions>): Double {
        var totalExpense = 0.0

        for (transaction in transactions) {
            if (transaction.ExpenseType == "Expense") {
                totalExpense = totalExpense.toDouble() + transaction.amount.toDouble()
            }
        }

        return totalExpense.toDouble()
    }

    private fun calculateTotalIncome(transactions: List<Transactions>): Double {
        var totalIncome = 0.0

        for (transaction in transactions) {
            if (transaction.ExpenseType == "Income") {
                totalIncome = totalIncome.toDouble() + transaction.amount.toDouble()
            }
        }

        return totalIncome.toDouble()
    }

    private fun displayExpenseAsPieChart(totalExpense: Double, totalIncome : Double) {
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        //val totalIncome = 100.0 // Replace with your logic to get total income

        if (totalIncome > 0) {
            // Calculate the percentage of total expense
            val percentage = (totalExpense / totalIncome) * 100

            // Create a slice for the total expense
            val expenseSlice = PieChart.Slice(percentage.toFloat(), Color.RED)
                //PieChart.Slice(percentage.toFloat(), Color.RED)
          // PieChart.BorderType(Color.BLUE)


            // Create a slice for the remaining income
            val incomeSlice = PieChart.Slice((100 - percentage).toFloat(), Color.GREEN)

            // Set the slices for the pie chart
            pieChart.slices = listOf(expenseSlice, incomeSlice)
        }
    }

    private fun displayExpenseAsPieChart(transactions: List<Transactions>) {
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        // Calculate the total expense for each unique label
        val expenseByLabel = mutableMapOf<String, Double>()
        for (transaction in transactions) {
            if (transaction.ExpenseType == "Expense") {
                val label = transaction.label
                val amount = transaction.amount.toDouble()
                expenseByLabel[label] = (expenseByLabel[label] ?: 0.0) + amount
            }
        }

        // Create slices for each unique label
        val slices = ArrayList<PieChart.Slice>()
        var colorIndex = 0
        val colors = intArrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW)

        for ((label, expense) in expenseByLabel) {
            val percentage = (expense / calculateTotalExpense(transactions)) * 100
            val slice = PieChart.Slice(percentage.toFloat(), colors[colorIndex % colors.size])
            slices.add(slice)
            if(colorIndex<3){
            colorIndex++}
            else
            {

            }

            // Create a TextView for the label
            val textView = TextView(this)
            textView.text = label
            textView.textSize = 12f
            textView.setTextColor(Color.BLACK)

            // Create a LinearLayout to hold the TextView
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.addView(textView)

            // Add the LinearLayout to the layout container
            findViewById<FrameLayout>(R.id.pieChart).addView(linearLayout)
        }

        // Set the slices for the pie chart
        pieChart.slices = slices
    }

}


