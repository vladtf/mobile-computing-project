package com.vti.mcproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.vti.mcproject.R
import com.vti.mcproject.ui.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailedTransactionScreen(
    txHash: String,
    transactionsViewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by transactionsViewModel.transactions.collectAsState()
    val transaction = transactions.find { it?.hash == txHash }

    if (transaction == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Transaction not found",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.label_amount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.balance_format, transaction.value),
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        
        HorizontalDivider()
        
        InfoField(stringResource(R.string.label_status), transaction.status.uppercase())
        InfoField(stringResource(R.string.label_hash), transaction.hash, useMonospace = true)
        InfoField(stringResource(R.string.label_from), transaction.sender, useMonospace = true)
        InfoField(stringResource(R.string.label_to), transaction.receiver, useMonospace = true)
        InfoField(
            stringResource(R.string.label_fee),
            stringResource(R.string.fee_format, transaction.fee)
        )
        InfoField(
            stringResource(R.string.label_gas),
            stringResource(R.string.gas_format, transaction.gasUsed.toString(), transaction.gasLimit.toString())
        )
        InfoField(stringResource(R.string.label_time), formatTimestamp(transaction.timestamp))
        
        if (!transaction.data.isNullOrEmpty()) {
            InfoField(stringResource(R.string.label_data), transaction.data)
        }
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String,
    useMonospace: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontFamily = if (useMonospace) FontFamily.Monospace else FontFamily.Default,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(date)
}