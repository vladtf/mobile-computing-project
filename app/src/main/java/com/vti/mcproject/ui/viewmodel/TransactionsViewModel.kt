package com.vti.mcproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.repository.AccountInfoRepository
import com.vti.mcproject.data.repository.TransactionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "TransactionsViewModel"

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            // Load account info and transactions in parallel
            val accountDeferred = async { accountInfoRepository.getAccountInfo() }
            val transactionsDeferred = async { transactionRepository.getAllTransactions() }

            // Await both results
            val accountResult = accountDeferred.await()
            val transactionsResult = transactionsDeferred.await()

            // Collect errors
            val errors = mutableListOf<String>()

            accountResult.onFailure { exception ->
                Log.e(TAG, "Error loading account info", exception)
                errors.add("Account: ${exception.message}")
            }

            transactionsResult.onFailure { exception ->
                Log.e(TAG, "Error loading transactions", exception)
                errors.add("Transactions: ${exception.message}")
            }

            // Set error message if any errors occurred
            if (errors.isNotEmpty()) {
                _errorMessage.value = errors.joinToString("\n")
            }

            _isLoading.value = false
        }
    }

    fun refresh() {
        loadData()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

