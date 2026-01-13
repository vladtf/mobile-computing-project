package com.vti.mcproject.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vti.mcproject.R
import com.vti.mcproject.ui.screens.DetailedTransactionScreen
import com.vti.mcproject.ui.screens.TransactionsScreen
import com.vti.mcproject.ui.viewmodel.TransactionsViewModel

enum class MCProjectScreen(@StringRes val title: Int) {
    Transactions(title = R.string.transactions_title),
    TransactionDetail(title = R.string.transaction_details_title)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCProjectAppBar(
    currentScreen: MCProjectScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onRefreshClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
            }
        },
        actions = {
            // Show refresh button only on Transactions screen
            if (onRefreshClick != null) {
                IconButton(
                    onClick = onRefreshClick,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(4.dp),
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
        }
    )
}

@Composable
fun MCProjectApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MCProjectScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("/") ?: MCProjectScreen.Transactions.name
    )

    // Shared ViewModel for transactions data
    val transactionsViewModel: TransactionsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val isLoading by transactionsViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            MCProjectAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onRefreshClick = if (currentScreen == MCProjectScreen.Transactions) {
                    { transactionsViewModel.refresh() }
                } else null,
                isLoading = isLoading
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MCProjectScreen.Transactions.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = MCProjectScreen.Transactions.name) {
                TransactionsScreen(
                    viewModel = transactionsViewModel,
                    onTransactionClick = { txHash ->
                        navController.navigate("${MCProjectScreen.TransactionDetail.name}/$txHash")
                    }
                )
            }

            composable(
                route = "${MCProjectScreen.TransactionDetail.name}/{txHash}",
                arguments = listOf(
                    navArgument("txHash") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val txHash = backStackEntry.arguments?.getString("txHash") ?: ""
                DetailedTransactionScreen(
                    txHash = txHash,
                    transactionsViewModel = transactionsViewModel
                )
            }
        }
    }
}
