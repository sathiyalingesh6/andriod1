package com.example

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.loginsignupauth.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*



class AddTransactionActivity : AppCompatActivity() {

    lateinit var arrayAdapter: ArrayAdapter<String>
     private lateinit var firebaseAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val amountInput = findViewById<TextInputEditText>(R.id.amountInput)
        val expense = findViewById<RadioButton>(R.id.expense)
        val income = findViewById<RadioButton>(R.id.income)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        val calendarDate = findViewById<TextInputEditText>(R.id.calendarDate)

        val labelInput = findViewById<AutoCompleteTextView>(R.id.labelInput)
        val addTransactionBtn = findViewById<Button>(R.id.addTransactionBtn)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInput)
        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val amountLayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val labelExpense = resources.getStringArray(R.array.labelExpense)

        val fstoredb = Firebase.firestore
        firebaseAuth = FirebaseAuth.getInstance()
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
        labelInput.setAdapter(arrayAdapter)

        expense.setOnClickListener {
            if (labelInput.text.toString() !in labelExpense.toList()) {
                labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        income.setOnClickListener {
            if (labelInput.text.toString() !in labelIncome.toList()) {
                labelInput.setText("")
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        labelInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                labelLayout.error = null
            }
        }

        amountInput.addTextChangedListener {
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        calendarDate.setText(SimpleDateFormat("EEEE, dd MMM yyyy").format(System.currentTimeMillis()))
        var date = Date()

        var cal = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "EEEE, dd MMM YYYY"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                calendarDate.setText(sdf.format(cal.time))
                date = cal.time

            }

        calendarDate.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
             val date = calendarDate.text.toString()
                var expenseType:String = "Income"
            if (expense.isChecked)
            {
                expenseType = "Expense"
            }

            val timestamp = Timestamp.now()
            val timeStampID = timestamp.toDate().time.toString()

            val expenses = mapOf(
                "ExpenseType" to expenseType,
                "label" to label,
                "description" to description,
                "amount" to amount,
                "date" to date,
                "timeStampID" to timeStampID
            )
            val temp = mapOf(
                timeStampID to expenses
            )
            //to fetch user id from firebase auth
            val uid = firebaseAuth.currentUser?.uid.toString()
            //to traverse into multiple subcollection
            //user to get into user collection and then into particular userid using uid
            val user = fstoredb.collection("user").document(uid)
            //user_expense to move inside the uid of that particular client and create a sub collection "expenses"
            val user_expense = user.collection("expenses")

            user_expense.document(timeStampID).set(expenses)
            Toast.makeText( this,"Error: Data not stored 1.", Toast.LENGTH_SHORT).show()


//
//            if (label.isEmpty())
//                labelLayout.error = "Please enter a valid label"
//            else if (amount == null)
//                amountLayout.error = "Please enter a valid amount"

        }

        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener {
            finish()
        }
    }




}