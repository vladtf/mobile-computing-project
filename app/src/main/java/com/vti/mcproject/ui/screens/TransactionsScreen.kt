package com.vti.mcproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vti.mcproject.R
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.ui.AppViewModelProvider
import com.vti.mcproject.ui.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val transactions by viewModel.transactions.collectAsState()
    val accountInfo by viewModel.accountInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val retryLabel = stringResource(R.string.action_retry)

    // Show error in Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = retryLabel,
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refresh()
            }
            viewModel.clearError()
        }
    }

    // Show detail screen if a transaction is selected
    selectedTransaction?.let { transaction ->
        DetailedTransactionScreen(
            transaction = transaction,
            onNavigateBack = { selectedTransaction = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.transactions_title)) },
                actions = {
                    IconButton(
                        onClick = { viewModel.refresh() },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.action_refresh)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.balance_format, accountInfo?.balance ?: "0"),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(transactions) { tx ->
                tx?.let {
                    TransactionItem(
                        transaction = it,
                        onClick = { selectedTransaction = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val statusColor = when (transaction.status.lowercase()) {
        "success" -> MaterialTheme.colorScheme.primary
        "pending" -> MaterialTheme.colorScheme.tertiary
        "failed", "invalid" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.balance_format, transaction.value),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = transaction.status.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = statusColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${shortenAddress(transaction.sender)} → ${shortenAddress(transaction.receiver)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
    }
}

private fun shortenAddress(address: String): String {
    return if (address.length > 13) {
        "${address.take(6)}...${address.takeLast(4)}"
    } else {
        address
    }
}