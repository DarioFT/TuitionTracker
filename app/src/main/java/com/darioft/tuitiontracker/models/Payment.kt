// Payment.kt
package com.darioft.tuitiontracker.models

data class Payment(
    val id: Int,
    val studentId: Int,
    val amount: Double,
    val date: String // Assuming the date is stored as a String
)
