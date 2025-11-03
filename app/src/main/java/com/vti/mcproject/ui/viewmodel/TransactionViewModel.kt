package com.vti.mcproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.repository.AccountInfoRepository
import com.vti.mcproject.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountInfoRepository: AccountInfoRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction?>>(emptyList())
    val transactions: StateFlow<List<Transaction?>> = _transactions.asStateFlow()

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _transactions.value = transactionRepository.getAllTransactions()
        _accountInfo.value = accountInfoRepository.getAccountInfo()
    }

    fun refresh() {
        loadData()
    }
}
