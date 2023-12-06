// DatabaseHelper.kt
package com.darioft.tuitiontracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.darioft.tuitiontracker.models.Payment
import com.darioft.tuitiontracker.models.Student

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TuitionTracker.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createStudentsTable = """
            CREATE TABLE students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                credits INTEGER NOT NULL
            );
        """.trimIndent()

        val createPaymentsTable = """
            CREATE TABLE payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students (id)
            );
        """.trimIndent()

        val createClassesTable = """
            CREATE TABLE classes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students (id)
            );
        """.trimIndent()

        db.execSQL(createStudentsTable)
        db.execSQL(createPaymentsTable)
        db.execSQL(createClassesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database version upgrades here
    }

    fun addStudent(student: Student): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", student.name)
            put("credits", student.credits)
        }
        val id = db.insert("students", null, values)
        db.close()
        return id // Returns the row ID of the newly inserted row, or -1 if an error occurred
    }

    fun getAllStudents(): List<Student> {
        val students = mutableListOf<Student>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM students"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("credits"))
                )
                students.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return students
    }

    fun updateStudentCredits(studentId: Int, newCredits: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("credits", newCredits)
        }
        db.update("students", values, "id = ?", arrayOf(studentId.toString()))
        db.close()
    }

    fun getStudentById(studentId: Int): Student {
        val db = this.readableDatabase
        val cursor =
            db.query("students", null, "id = ?", arrayOf(studentId.toString()), null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val student = Student(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("name")),
                cursor.getInt(cursor.getColumnIndex("credits"))
            )
            cursor.close()
            return student
        }
        db.close()
        throw Exception("Student not found") // Or handle this scenario appropriately
    }

    fun getPaymentsForStudent(studentId: Int): List<Payment> {
        val payments = mutableListOf<Payment>()
        val db = this.readableDatabase
        val cursor = db.query(
            "payments",
            null,
            "student_id = ?",
            arrayOf(studentId.toString()),
            null,
            null,
            "date DESC"  // Assuming you want to sort by date, newest first
        )

        if (cursor.moveToFirst()) {
            do {
                val payment = Payment(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("student_id")),
                    cursor.getDouble(cursor.getColumnIndex("amount")),
                    cursor.getString(cursor.getColumnIndex("date"))
                )
                payments.add(payment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return payments
    }

    fun addPaymentForStudent(studentId: Int, amount: Double, date: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("student_id", studentId)
            put("amount", amount)
            put("date", date)
        }
        db.insert("payments", null, values)
        db.close()
    }

    fun updateStudentName(studentId: Int, newName: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("name", newName)
        }
        db.update("students", values, "id = ?", arrayOf(studentId.toString()))
        db.close()
    }


}
