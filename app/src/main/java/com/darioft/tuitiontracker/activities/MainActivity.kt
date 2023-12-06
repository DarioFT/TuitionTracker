package com.darioft.tuitiontracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.darioft.tuitiontracker.database.DatabaseHelper
import com.darioft.tuitiontracker.R
import com.darioft.tuitiontracker.models.Student
import com.darioft.tuitiontracker.adapters.StudentAdapter
import com.darioft.tuitiontracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        loadStudents()

        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStudents() // Reload students every time MainActivity resumes
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            studentAdapter = StudentAdapter(listOf()) { student ->
                val intent = Intent(this@MainActivity, StudentDetailActivity::class.java)
                intent.putExtra("STUDENT_ID", student.id)
                startActivity(intent)
            }
            adapter = studentAdapter
        }
    }

    private fun loadStudents() {
        val students = fetchStudentsFromDatabase()
        if (students.isEmpty()) {
            findViewById<TextView>(R.id.textViewEmptyStudents).visibility = View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textViewEmptyStudents).visibility = View.GONE
        }
        studentAdapter.updateStudents(students)
    }

    // Function to fetch students from the database
    private fun fetchStudentsFromDatabase(): List<Student> {
        val databaseHelper = DatabaseHelper(this)
        return databaseHelper.getAllStudents()
    }

}
