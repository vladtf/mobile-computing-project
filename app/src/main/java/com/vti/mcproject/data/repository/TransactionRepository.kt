package com.vti.mcproject.data.repository

import com.vti.mcproject.blockchain.MultiversXSdkService
import com.vti.mcproject.data.model.Transaction

class TransactionRepository {
    
    private val multiversXService = MultiversXSdkService()
    
    suspend fun getAllTransactions(address: String = MultiversXSdkService.CONTRACT_ADDRESS): Result<List<Transaction>> {
        return multiversXService.getTransactions(address)
    }
}