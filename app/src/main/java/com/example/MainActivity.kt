package com.example

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginsignupauth.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    var transactionList = ArrayList<Transactions>()
    var income = 0.0
    var expense = 0.0
    var balance = 0.0
     lateinit var income1 : TextView
    lateinit var expense1 : TextView
    lateinit var balance1 : TextView


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var addBtn = findViewById<FloatingActionButton>(R.id.addBtn)
        var income =0.0
        var expense=0.0
        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
//        redirect()
        //transactionAdapter = transactionList
        //recycler view
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //val ine : Number= income - expense
        expenseAdapter = ExpenseAdapter { expense ->
            // Handle the item click here
            // You can now access the clicked expense data
            val label = expense.label
            val description = expense.description
            val amount = expense.amount
            val exptype = expense.ExpenseType
            val date = expense.date
            val timestamp = expense.timestampID
            // Use the data as needed (e.g., pass it to a detail activity or fragment)

            val intent = Intent(this, Update_delete_Transactions::class.java)
            intent.putExtra("label",label)
            intent.putExtra("description",description)
            intent.putExtra("amount",amount.toString())
            intent.putExtra("exptype",exptype)
            intent.putExtra("date",date)
            intent.putExtra("timestamp",timestamp)
            startActivity(intent)
        }

        //to pass to piechart
        val stats = findViewById<ImageButton>(R.id.stats)
        stats.setOnClickListener()
        {
            val pie_intent = Intent(this, pie_charttt::class.java)
            //pie_intent.putExtra("transactionList", transactionList)
            startActivity(pie_intent)
        }


        retriveexpenses()



        //val d = income.toString()


        //balance.text = "$ine"

    }
//    private fun redirect()
//    {
//        val i = Intent(this,SignupActivity::class.java)
//        startActivity(i)
//        finish()


    private fun retriveexpenses() {
//        income=10.0
//        expense=10.0
        val firestoredb = FirebaseFirestore.getInstance()
        val firebaseauth = FirebaseAuth.getInstance()
        val userId = firebaseauth.currentUser?.uid.toString()
        //to fetch userid.user table
        val user = firestoredb.collection("user").document(userId)
        //toget into expense
        val expensecollec = user.collection("expenses")


        //to fetch data into array list
        expensecollec.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot != null) {

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
                    transactionList.add(trans)

                    expenseAdapter.submitList(transactionList)
                    recyclerView.adapter = expenseAdapter
                    income = 0.0
                    expense = 0.0

                    for(exp in transactionList) {
                        if (exp.ExpenseType == "Income") {
                            income =  income + exp.amount.toDouble()
                            //Toast.makeText(this, "${income}", Toast.LENGTH_SHORT).show()

                        } else {
                            expense = expense + exp.amount.toDouble()
                        }
                    }
                    income1 = findViewById(R.id.income_home)
                    expense1 = findViewById(R.id.expense_home)

                    income1.text = "$income"
                    expense1.text = "$expense"
                    balance = income - expense
                    balance1 = findViewById(R.id.balance)
                    balance1.text = "$balance"


                }
            }
            val progressBar = findViewById<ProgressBar>(R.id.progress_bar_spending)
            //val exp = expense1.text.toString().toDoubleOrNull()
            //val inc= income1.text.toString().toDoubleOrNull()

            val maxProgress = progressBar.max
            val mul = (income/expense);
            val progress = (maxProgress * mul * 10) / 100
            val mul1 = progress * 10
            val format = "%.0f".format(mul1)
            val prece = findViewById<TextView>(R.id.tv_progress)
            prece.text = "    "+ format + "%"
            Toast.makeText(this,"budgetttt $progress",Toast.LENGTH_SHORT).show()

            // Set the progress of the ProgressBar * \\
            progressBar.progress = progress.toInt()
        }

        
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        return true
    }
}



private fun SearchView?.setOnQueryTextListener(mainActivity: MainActivity) {

}



