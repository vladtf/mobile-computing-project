package com.vti.mcproject.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vti.mcproject.MCProjectApplication
import com.vti.mcproject.ui.viewmodel.TransactionsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire MCProject app.
 * This centralizes ViewModel creation with proper dependency injection.
 */
object AppViewModelProvider {

    val Factory = viewModelFactory {
        // Initializer for TransactionsViewModel
        initializer {
            TransactionsViewModel(
                transactionRepository = mcProjectApplication().container.transactionRepository,
                accountInfoRepository = mcProjectApplication().container.accountInfoRepository
            )
        }
    }
}

/**
 * Extension function to query for [Application] object and returns an instance of
 * [MCProjectApplication].
 */
fun CreationExtras.mcProjectApplication(): MCProjectApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MCProjectApplication)
