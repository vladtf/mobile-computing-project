package com.vti.mcproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vti.mcproject.blockchain.MultiversXSdkService
import com.vti.mcproject.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying MultiversX transactions in a RecyclerView
 */
class TransactionAdapter : ListAdapter<MultiversXSdkService.Transaction, TransactionAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        fun bind(transaction: MultiversXSdkService.Transaction) {
            // Show shortened hash
            binding.textTxHash.text = shortenAddress(transaction.hash)
            
            // Show shortened addresses
            binding.textSender.text = shortenAddress(transaction.sender)
            binding.textReceiver.text = shortenAddress(transaction.receiver)
            
            // Show value in EGLD
            binding.textValue.text = "${transaction.value} EGLD"
            
            // Show status with color
            binding.textStatus.text = transaction.status.uppercase()
            val context = binding.root.context
            binding.textStatus.setBackgroundColor(
                when (transaction.status.lowercase()) {
                    "success" -> androidx.core.content.ContextCompat.getColor(context, R.color.transaction_status_success)
                    "pending" -> androidx.core.content.ContextCompat.getColor(context, R.color.transaction_status_pending)
                    "failed", "invalid" -> androidx.core.content.ContextCompat.getColor(context, R.color.transaction_status_failed)
                    else -> androidx.core.content.ContextCompat.getColor(context, R.color.transaction_status_default)
                }
            )
            
            // Show fee
            binding.textFee.text = "Fee: ${transaction.fee} EGLD"
            
            // Show timestamp
            val date = Date(transaction.timestamp * 1000) // Convert seconds to milliseconds
            binding.textTimestamp.text = dateFormat.format(date)
        }

        private fun shortenAddress(address: String): String {
            return if (address.length > 13) {
                "${address.take(8)}...${address.takeLast(5)}"
            } else {
                address
            }
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<MultiversXSdkService.Transaction>() {
        override fun areItemsTheSame(
            oldItem: MultiversXSdkService.Transaction,
            newItem: MultiversXSdkService.Transaction
        ): Boolean {
            return oldItem.hash == newItem.hash
        }

        override fun areContentsTheSame(
            oldItem: MultiversXSdkService.Transaction,
            newItem: MultiversXSdkService.Transaction
        ): Boolean {
            return oldItem == newItem
        }
    }
}
