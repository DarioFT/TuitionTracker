// StudentDetailActivity.kt
package com.darioft.tuitiontracker.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darioft.tuitiontracker.*
import com.darioft.tuitiontracker.adapters.PaymentsAdapter
import com.darioft.tuitiontracker.database.DatabaseHelper
import com.darioft.tuitiontracker.dialogs.AddPaymentDialog
import com.darioft.tuitiontracker.models.Payment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class StudentDetailActivity : AppCompatActivity() {
    private lateinit var textViewStudentName: TextView
    private lateinit var textViewCredits: TextView
    private var studentId: Int = 0

    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var recyclerViewPayments: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.student_detail_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewStudentName = findViewById(R.id.textViewStudentName)
        textViewCredits = findViewById(R.id.textViewCredits)

        studentId = intent.getIntExtra("STUDENT_ID", 0)
        loadStudentDetails(studentId)

        // Button for deducting credits
        val buttonEditName: AppCompatImageButton = findViewById(R.id.buttonEditName)
        buttonEditName.setOnClickListener {
            editName(studentId)
        }

        // Setup RecyclerView for payments
        recyclerViewPayments = findViewById(R.id.recyclerViewPayments)
        setupPaymentsRecyclerView()

        // Button for deducting credits
        val buttonDeductCredit: Button = findViewById(R.id.buttonDeductCredit)
        buttonDeductCredit.setOnClickListener {
            deductCredit(studentId)
        }

        // Button for adding credits
        val buttonAddCredit: Button = findViewById(R.id.buttonAddCredit)
        buttonAddCredit.setOnClickListener {
            addCredit(studentId)
        }

        // FAB for adding payments
        val fabAddPayment: FloatingActionButton = findViewById(R.id.fabAddPayment)
        fabAddPayment.setOnClickListener {
            // Open dialog or activity to add payment
            openAddPaymentDialog()
        }
    }

    private fun setupPaymentsRecyclerView() {
        recyclerViewPayments.layoutManager = LinearLayoutManager(this)
        paymentsAdapter = PaymentsAdapter(listOf())
        recyclerViewPayments.adapter = paymentsAdapter
        loadPayments(studentId)
    }

    private fun deductCredit(studentId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val student = databaseHelper.getStudentById(studentId)
        if (student.credits > 0) {
            databaseHelper.updateStudentCredits(studentId, student.credits - 1)
            loadStudentDetails(studentId) // Refresh student details
        }
    }

    private fun addCredit(studentId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val student = databaseHelper.getStudentById(studentId)
        databaseHelper.updateStudentCredits(studentId, student.credits + 1)
        loadStudentDetails(studentId) // Refresh student details

    }

    private fun loadPayments(studentId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val payments = databaseHelper.getPaymentsForStudent(studentId)
        if (payments.isEmpty()) {
            findViewById<TextView>(R.id.textViewEmptyPayments).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textViewEmptyPayments).visibility = View.GONE
        }
        paymentsAdapter.updatePayments(payments)
    }

    // Part of StudentDetailActivity.kt
    private fun openAddPaymentDialog() {
        val dialog = AddPaymentDialog(studentId) { payment, credits ->
            addPaymentAndCreditsToDatabase(payment, credits)
            loadStudentDetails(studentId)  // Refresh student details
            loadPayments(studentId)        // Refresh payments list
        }
        dialog.show(supportFragmentManager, "AddPaymentDialog")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the current activity and return to MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadStudentDetails(studentId: Int) {
        val databaseHelper = DatabaseHelper(this)
        val student = databaseHelper.getStudentById(studentId)
        textViewStudentName.text = student.name
        textViewCredits.text = "Credits: ${student.credits}"
    }

    private fun addPaymentAndCreditsToDatabase(payment: Payment, credits: Int) {
        val databaseHelper = DatabaseHelper(this)
        databaseHelper.addPaymentForStudent(payment.studentId, payment.amount, payment.date)
        val student = databaseHelper.getStudentById(payment.studentId)
        databaseHelper.updateStudentCredits(payment.studentId, student.credits + credits)
    }

    private fun editName(studentId: Int) {
        // Open a dialog to enter the new name
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_name, null)
        val editTextNewName = dialogView.findViewById<EditText>(R.id.editTextNewName)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Name")
            .setPositiveButton("Save") { _, _ ->
                val newName = editTextNewName.text.toString()
                if (newName.isNotEmpty()) {
                    val databaseHelper = DatabaseHelper(this)
                    databaseHelper.updateStudentName(studentId, newName)
                    loadStudentDetails(studentId) // Refresh student details
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Delete") { _, _ ->
                showDeleteConfirmationDialog(studentId)
            }
            .show()
    }

    private fun showDeleteConfirmationDialog(studentId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this student?")
            .setPositiveButton("Yes") { _, _ ->
                val databaseHelper = DatabaseHelper(this)
                databaseHelper.deleteStudent(studentId)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }


}
