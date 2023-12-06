package com.darioft.tuitiontracker.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.darioft.tuitiontracker.R
import com.darioft.tuitiontracker.models.Payment
import java.util.*

class AddPaymentDialog(
    private val studentId: Int,
    private val onPaymentAdded: (Payment, Int) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_add_payment, null)
        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextAmount)
        val buttonDatePicker = dialogView.findViewById<Button>(R.id.buttonDatePicker)
        val buttonAdd = dialogView.findViewById<Button>(R.id.buttonAddPayment)
        var selectedDate = ""
        val editTextCredits = dialogView.findViewById<EditText>(R.id.editTextCredits)
        editTextCredits.setText("4")  // Default value for credits

        buttonDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    buttonDatePicker.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonAdd.setOnClickListener {
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val credits = editTextCredits.text.toString().toIntOrNull()
            if (amount != null && credits != null && selectedDate.isNotEmpty()) {
                val newPayment = Payment(0, studentId, amount, selectedDate)
                onPaymentAdded(newPayment, credits)
                dismiss()
            }
            // Handle invalid input
        }

        return Dialog(requireContext()).apply {
            setContentView(dialogView)
            setTitle("Add Payment")
        }
    }
}
