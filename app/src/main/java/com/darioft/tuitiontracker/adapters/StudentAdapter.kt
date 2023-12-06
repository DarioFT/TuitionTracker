// StudentAdapter.kt
package com.darioft.tuitiontracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darioft.tuitiontracker.models.Student
import com.darioft.tuitiontracker.databinding.ItemStudentBinding

class StudentAdapter(private var students: List<Student>, private val clickListener: (Student) -> Unit) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {
    class StudentViewHolder(private val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: Student, isFirstItem: Boolean = false) {
            binding.textViewName.text = student.name
            binding.textViewCredits.text = "${student.credits}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student, position == 0)
        holder.itemView.setOnClickListener { clickListener(student) } // Add click listener
    }

    override fun getItemCount(): Int = students.size

    fun updateStudents(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged() // Notify the adapter that the data set has changed
    }
}
