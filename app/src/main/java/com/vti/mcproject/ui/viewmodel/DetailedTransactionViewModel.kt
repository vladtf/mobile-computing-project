package com.vti.mcproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.vti.mcproject.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DetailedTransactionViewModel : ViewModel() {

    private val _transaction = MutableStateFlow<Transaction?>(null)
    val transaction: StateFlow<Transaction?> = _transaction.asStateFlow()

    fun setTransaction(transaction: Transaction) {
        _transaction.value = transaction
    }

    fun clearTransaction() {
        _transaction.value = null
    }
}

