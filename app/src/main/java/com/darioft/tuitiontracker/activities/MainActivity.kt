package com.darioft.tuitiontracker.activities

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darioft.tuitiontracker.database.DatabaseHelper
import com.darioft.tuitiontracker.R
import com.darioft.tuitiontracker.models.Student
import com.darioft.tuitiontracker.adapters.StudentAdapter
import com.darioft.tuitiontracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentAdapter: StudentAdapter

    private val iconWidth = 100 // Adjust this value as needed
    private val iconMargin = -50 // Adjust this value as needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        loadStudents()

        binding.fab.setOnClickListener {
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

            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false // Not used
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    val itemView = viewHolder.itemView
                    val itemHeight = itemView.bottom - itemView.top
                    val paint = Paint()

                    // Limit the swipe distance
                    val limitedDX = dX.coerceIn(-iconWidth.toFloat(), 0f)

                    if (limitedDX < 0) { // Swipe to the left
                        // Background for minus sign
                        paint.color = ContextCompat.getColor(this@MainActivity, R.color.light_gray) // Background color
                        val background = RectF(itemView.right + limitedDX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, paint)

                        // Drawing minus sign
                        paint.color = ContextCompat.getColor(this@MainActivity, R.color.white) // Minus sign color
                        paint.strokeWidth = 8f // Adjust the thickness of the minus sign
                        val minusSignLength = iconWidth / 2 // Adjust as needed
                        val centerY = itemView.top + (itemHeight / 2)
                        val minusSignStartX = itemView.right + limitedDX - iconMargin - minusSignLength / 2
                        val minusSignEndX = itemView.right + limitedDX - iconMargin + minusSignLength / 2
                        c.drawLine(
                            minusSignStartX,
                            centerY.toFloat(),
                            minusSignEndX,
                            centerY.toFloat(),
                            paint
                        )
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, limitedDX, dY, actionState, isCurrentlyActive)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val student = studentAdapter.students[position]

                    if (student.credits > 0) {
                        // Decrease credits and prevent them from going below zero
                        val newCredits = maxOf(student.credits - 1, 0)
                        studentAdapter.updateStudentCredits(student, newCredits)

                        // Update the database
                        val databaseHelper = DatabaseHelper(this@MainActivity)
                        databaseHelper.updateStudentCredits(student.id, newCredits)

                        Toast.makeText(this@MainActivity, "Credits discounted", Toast.LENGTH_SHORT).show()
                    } else {
                        // Reset swipe if credits are already at zero
                        studentAdapter.notifyItemChanged(position)
                        Toast.makeText(this@MainActivity, "No credits available", Toast.LENGTH_SHORT).show()
                    }
                }

            })

            itemTouchHelper.attachToRecyclerView(binding.recyclerView)
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
