package com.vti.mcproject.data.repository

import com.vti.mcproject.data.local.dao.TransactionDao
import com.vti.mcproject.data.network.MultiversXService
import com.vti.mcproject.data.model.Transaction

class TransactionRepository(
    private val multiversXService: MultiversXService = MultiversXService(),
    private val transactionDao: TransactionDao
) {

    fun observeTransactions(address: String = MultiversXService.CONTRACT_ADDRESS) =
        transactionDao.getByAddress(address)

    suspend fun getTransactionByHash(hash: String): Transaction? =
        transactionDao.getByHash(hash)

    suspend fun getAllTransactions(address: String = MultiversXService.CONTRACT_ADDRESS): Result<List<Transaction>> {
        return multiversXService.getTransactions(address)
            .also { result ->
                result.onSuccess { transactions ->
                    transactionDao.deleteAll()
                    transactionDao.insert(transactions)
                }
            }
    }
}