// PaymentsAdapter.kt
package com.darioft.tuitiontracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.darioft.tuitiontracker.models.Payment
import com.darioft.tuitiontracker.databinding.ItemPaymentBinding

class PaymentsAdapter(private var payments: List<Payment>) : RecyclerView.Adapter<PaymentsAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(private val binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(payment: Payment) {
            binding.textViewAmount.text = "$ ${payment.amount}"
            binding.textViewDate.text = "${payment.date}"
            // Bind other payment details as needed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(payments[position])
    }

    override fun getItemCount(): Int = payments.size

    fun updatePayments(newPayments: List<Payment>) {
        payments = newPayments
        notifyDataSetChanged()
    }
}
