package com.vti.mcproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.repository.AccountInfoRepository
import com.vti.mcproject.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailedTransactionViewModel(
    private val transactionRepository: TransactionRepository = TransactionRepository(),
    private val accountInfoRepository: AccountInfoRepository = AccountInfoRepository()
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> = _accountInfo.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Load account info
            accountInfoRepository.getAccountInfo().fold(
                onSuccess = { accountInfo ->
                    _accountInfo.value = accountInfo
                },
                onFailure = { exception ->
                    println("Error loading account info: ${exception.message}")
                }
            )
            
            // Load transactions
            transactionRepository.getAllTransactions().fold(
                onSuccess = { transactions ->
                    _transactions.value = transactions
                },
                onFailure = { exception ->
                    println("Error loading transactions: ${exception.message}")
                }
            )
        }
    }

    fun refresh() {
        loadData()
    }
}

