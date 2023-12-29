
package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.addTextChangedListener
import com.example.loginsignupauth.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Update_delete_Transactions : AppCompatActivity() {
    private lateinit var description: TextInputEditText
    private lateinit var amount: TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var calendarDate: TextInputEditText
    private var selectedDate: Calendar = Calendar.getInstance()
    lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var firebaseAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_delete_transactions)

        description = findViewById(R.id.descriptionInputEdit)
        amount = findViewById(R.id.amountInputEdit)
        radioGroup = findViewById(R.id.radioGroupEdit)
        calendarDate = findViewById(R.id.calendarDateEdit)

        val UpdateTransactionBtn = findViewById<Button>(R.id.updateBtnEdit)
        val deleteBtn = findViewById<Button>(R.id.deleteBtnEdit)
        val amountInput = findViewById<TextInputEditText>(R.id.amountInputEdit)
        val descriptionInput = findViewById<TextInputEditText>(R.id.descriptionInputEdit)
        val expense = findViewById<RadioButton>(R.id.expenseEdit)
        val income = findViewById<RadioButton>(R.id.incomeEdit)
        val labelInput = findViewById<AutoCompleteTextView>(R.id.labelInputEdit)
        val labelExpense = resources.getStringArray(R.array.labelExpense)
        val labelIncome = resources.getStringArray(R.array.labelIncome)
        val labelLayout = findViewById<TextInputLayout>(R.id.labelLayoutEdit)

        val amt = intent.getStringExtra("amount")
        val desc = intent.getStringExtra("description")
        val exptype = intent.getStringExtra("exptype")
        val date1 = intent.getStringExtra("date")
        val label = intent.getStringExtra("label")
        val timestamp = intent.getStringExtra("timestamp")

        val fstoredb = Firebase.firestore
        //val userid = firebaseAuth.currentUser?.uid.toString()



        Toast.makeText(this, "$timestamp", Toast.LENGTH_SHORT).show()

        description.setText(desc)

        if (exptype != null) {
            if (exptype.contains("Income")) {
                radioGroup.check(R.id.incomeEdit)
            } else {
                radioGroup.check(R.id.expenseEdit)
            }
        }

        if (amt != null) {
            amount.setText(amt)
        }

        // Initialize the selected date with the date from the intent
        if (date1 != null) {
            val date2 = parseFirestoreDate(date1)
            selectedDate = date2
            calendarDate.setText(
                SimpleDateFormat(
                    "EEEE, dd MMM yyyy",
                    Locale.ENGLISH
                ).format(date2.time)
            )
        }

        calendarDate.setOnClickListener {
            showDatePickerDialog()
        }

        labelInput.setText(label)
        //adapter
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
        labelInput.setAdapter(arrayAdapter)

        expense.setOnClickListener {
            if (labelInput.text.toString() !in labelExpense.toList()) {
                labelInput.setText(label)
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelExpense)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        income.setOnClickListener {
            if (labelInput.text.toString() !in labelIncome.toList()) {
                labelInput.setText(label)
                arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, labelIncome)
                labelInput.setAdapter(arrayAdapter)
            }
        }

        labelInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                labelLayout.error = null
            }
        }

        amount.addTextChangedListener {
            if (it!!.isNotEmpty())
                amount.error = null
        }

        UpdateTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val date = calendarDate.text.toString()
            var expenseType: String = "Income"
            if (expense.isChecked) {
                expenseType = "Expense"
            }



            val expenses = mapOf(
                "ExpenseType" to expenseType,
                "label" to label,
                "description" to description,
                "amount" to amount,
                "date" to date,
                "timeStampID" to timestamp
            )

            firebaseAuth = FirebaseAuth.getInstance()
            //to fetch user id from firebase auth
            val uid = firebaseAuth.currentUser?.uid.toString()
            //to traverse into multiple subcollection
            //user to get into user collection and then into particular userid using uid
            val user = fstoredb.collection("user").document(uid)
            //user_expense to move inside the uid of that particular client and create a sub collection "expenses"
            val user_expense = user.collection("expenses")

            user_expense.document(timestamp.toString()).set(expenses)
            Toast.makeText(this, "Updated Successfully.", Toast.LENGTH_SHORT).show()




            //notification
            val CHANNEL_ID = "my_channel_id" // Replace with your desired channel ID
            val CHANNEL_NAME = "My Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

// Create a notification
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_lock_open_24)
                .setContentTitle("Updated Successfully")
                .setContentText("Transaction Updated Successfully")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

// Build and display the notification
            val notificationId = 1 // Replace with a unique notification ID
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            NotificationManagerCompat.from(this).notify(notificationId, builder.build())


            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)


//
//            if (label.isEmpty())
//                labelLayout.error = "Please enter a valid label"
//            else if (amount == null)
//                amountLayout.error = "Please enter a valid amount"

        }

        //delete
        deleteBtn.setOnClickListener {


            firebaseAuth = FirebaseAuth.getInstance()
            //to fetch user id from firebase auth
            val uid = firebaseAuth.currentUser?.uid.toString()
            //to traverse into multiple subcollection
            //user to get into user collection and then into particular userid using uid
            val user = fstoredb.collection("user").document(uid)
            //user_expense to move inside the uid of that particular client and create a sub collection "expenses"
            val user_expense = user.collection("expenses")

            user_expense.document(timestamp.toString()).delete().addOnSuccessListener {
                Toast.makeText(this, "Data Deleted Successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)

            }

        }

        val closeBtn = findViewById<ImageButton>(R.id.closeBtnEdit)
        closeBtn.setOnClickListener {
            finish()
        }

    }
        private fun parseFirestoreDate(date: String): Calendar {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)

        if (parsedDate != null) {
            cal.time = parsedDate
        }

        return cal
    }

    private fun showDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                calendarDate.setText(
                    SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH).format(selectedDate.time)
                )
            }

        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
