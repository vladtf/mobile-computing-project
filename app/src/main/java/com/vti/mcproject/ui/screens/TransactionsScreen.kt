package com.vti.mcproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.repository.AccountInfoRepository
import com.vti.mcproject.data.repository.TransactionRepository
import com.vti.mcproject.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel = viewModel {
        TransactionViewModel(
            transactionRepository = TransactionRepository(),
            accountInfoRepository = AccountInfoRepository()
        )
    }
) {
    val transactions by viewModel.transactions.collectAsState()
    val accountInfo by viewModel.accountInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiversX Transactions") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Balance: ${accountInfo?.balance ?: "..."} EGLD",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Text(
                    text = "Transactions (${transactions.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(transactions) { tx ->
                TransactionItem(tx)
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction?) {
    if (transaction == null) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        return
    }

    Text(
        text = buildString {
            append("${transaction.value} EGLD • ")
            append(transaction.status.uppercase())
            append("\n")
            append("${transaction.sender} → ${transaction.receiver}")
        },
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}