package com.vti.mcproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.ui.viewmodel.DetailedTransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedTransactionScreen(
    transaction: Transaction,
    onNavigateBack: () -> Unit = {},
    viewModel: DetailedTransactionViewModel = viewModel()
) {
    // Set the transaction in ViewModel when screen is displayed
    LaunchedEffect(transaction) {
        viewModel.setTransaction(transaction)
    }

    // Clear transaction when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearTransaction()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Amount - prominent display
            Column {
                Text(
                    text = "Amount",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${transaction.value} EGLD",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            HorizontalDivider()
            
            InfoField("Status", transaction.status.uppercase())
            InfoField("Hash", transaction.hash)
            InfoField("From", transaction.sender)
            InfoField("To", transaction.receiver)
            InfoField("Fee", "${transaction.fee} EGLD")
            InfoField("Gas", "${transaction.gasUsed} / ${transaction.gasLimit}")
            InfoField("Time", formatTimestamp(transaction.timestamp))
            
            if (!transaction.data.isNullOrEmpty()) {
                InfoField("Data", transaction.data)
            }
        }
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = if (label in listOf("Hash", "From", "To")) FontFamily.Monospace else FontFamily.Default,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(date)
}