package com.vti.mcproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vti.mcproject.R
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
                title = { Text(stringResource(R.string.transaction_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
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