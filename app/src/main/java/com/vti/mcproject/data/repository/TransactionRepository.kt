package com.vti.mcproject.data.repository

import com.vti.mcproject.data.network.MultiversXService
import com.vti.mcproject.data.model.Transaction

class TransactionRepository {
    
    private val multiversXService = MultiversXService()
    
    suspend fun getAllTransactions(address: String = MultiversXService.CONTRACT_ADDRESS): Result<List<Transaction>> {
        return multiversXService.getTransactions(address)
    }
}