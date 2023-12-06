// AddStudentActivity.kt
package com.darioft.tuitiontracker.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darioft.tuitiontracker.database.DatabaseHelper
import com.darioft.tuitiontracker.R
import com.darioft.tuitiontracker.models.Student

class AddStudentActivity : AppCompatActivity() {
    private lateinit var editTextStudentName: EditText
    private lateinit var editTextCredits: EditText
    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_student_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextStudentName = findViewById(R.id.editTextStudentName)
        editTextCredits = findViewById(R.id.editTextCredits)
        buttonSave = findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            saveStudent()
        }
    }

    // AddStudentActivity.kt and StudentDetailActivity.kt
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Close the current activity and return to MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun saveStudent() {
        val name = editTextStudentName.text.toString().trim()
        val credits = editTextCredits.text.toString().toIntOrNull()

        if (name.isEmpty() || credits == null) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Code to save the student to the database
        val newStudent = Student(0, name, credits) // Assuming Student has an ID, name, and credits
        insertStudentIntoDatabase(newStudent)

        Toast.makeText(this, "Student saved", Toast.LENGTH_SHORT).show()
        finish() // Close the activity
    }

    // Part of AddStudentActivity.kt
    private fun insertStudentIntoDatabase(student: Student) {
        val databaseHelper = DatabaseHelper(this)
        val id = databaseHelper.addStudent(student)
        if (id == -1L) {
            // Handle error (e.g., show a toast message)
            Toast.makeText(this, "Error saving student", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
        }
    }

}
