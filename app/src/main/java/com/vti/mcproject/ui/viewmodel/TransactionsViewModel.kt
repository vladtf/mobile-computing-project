package com.vti.mcproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.repository.AccountInfoRepository

import com.vti.mcproject.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val transactionRepository: TransactionRepository,
    private val accountInfoRepository: AccountInfoRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> =
        transactionRepository.observeTransactions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val accountInfo: StateFlow<AccountInfo?> =
        accountInfoRepository.observeAccountInfo()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Load account info
            accountInfoRepository.getAccountInfo().fold(
                onSuccess = { accountInfo ->
                    // No need to set value, as we are observing the database
                },
                onFailure = { exception ->
                    println("Error loading account info: ${exception.message}")
                }
            )
            
            // Load transactions
            transactionRepository.getAllTransactions().fold(
                onSuccess = { transactions ->
                    // No need to set value, as we are observing the database
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

