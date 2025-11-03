package com.vti.mcproject.data.model

import android.util.Log
import com.vti.mcproject.utils.CurrencyUtils
import org.json.JSONArray

data class Transaction(
    val hash: String,
    val sender: String,
    val receiver: String,
    val value: String,
    val timestamp: Long,
    val status: String,
    val fee: String,
    val data: String? = null,
    val gasUsed: Long = 0,
    val gasLimit: Long = 0
) {
    companion object {
        private const val TAG = "Transaction"
        
        /**
         * Parse transactions from the MultiversX /transfers API response
         */
        fun parseFromJson(jsonString: String): List<Transaction> {
            return try {
                val jsonArray = JSONArray(jsonString)
                val transactions = mutableListOf<Transaction>()
                
                for (i in 0 until jsonArray.length()) {
                    val txJson = jsonArray.getJSONObject(i)
                    
                    val value = txJson.optString("value", "0")
                    val txHash = txJson.optString("txHash", txJson.optString("originalTxHash", ""))
                    val sender = txJson.optString("sender", "")
                    val receiver = txJson.optString("receiver", "")
                    val timestamp = txJson.optLong("timestamp", 0L)
                    val status = txJson.optString("status", "unknown")
                    
                    val transaction = Transaction(
                        hash = txHash,
                        sender = sender,
                        receiver = receiver,
                        value = CurrencyUtils.convertWeiToEgld(value),
                        timestamp = timestamp,
                        status = status,
                        fee = "0",
                        data = "",
                        gasUsed = 0L,
                        gasLimit = 0L
                    )
                    
                    transactions.add(transaction)
                }
                
                transactions
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing transactions", e)
                emptyList()
            }
        }
    }
}