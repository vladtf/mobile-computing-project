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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vti.mcproject.blockchain.MultiversXSdkService

private val mockAccountInfo = MultiversXSdkService.AccountInfo(
    address = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma",
    balance = "1234.567",
    nonce = 42
)

private val mockTransactions = listOf(
    MultiversXSdkService.Transaction(
        hash = "abc123", sender = "erd1sender", receiver = "erd1receiver",
        value = "100.5", timestamp = 1699000000L, status = "success",
        fee = "0.001", data = "", gasUsed = 50000L, gasLimit = 60000L
    ),
    MultiversXSdkService.Transaction(
        hash = "xyz987", sender = "erd1alice", receiver = "erd1bob",
        value = "50.25", timestamp = 1698900000L, status = "pending",
        fee = "0.0005", data = "", gasUsed = 30000L, gasLimit = 40000L
    ),
    MultiversXSdkService.Transaction(
        hash = "mno456", sender = "erd1charlie", receiver = "erd1dave",
        value = "25.0", timestamp = 1698800000L, status = "failed",
        fee = "0.0008", data = "", gasUsed = 45000L, gasLimit = 50000L
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MultiversX Transactions") },
                actions = {
                    IconButton(onClick = { }) {
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
                    text = "Balance: ${mockAccountInfo.balance} EGLD",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Text(
                    text = "Transactions (${mockTransactions.size})",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(mockTransactions) { tx ->
                TransactionItem(tx)
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: MultiversXSdkService.Transaction) {
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