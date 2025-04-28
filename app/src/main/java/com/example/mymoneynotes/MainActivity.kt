package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.ui.screens.*
import com.example.mymoneynotes.ui.theme.MyMoneyNotesAppTheme
import com.example.mymoneynotes.ui.viewmodels.TransactionViewModel
import com.example.mymoneynotes.ui.viewmodels.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read user preference for dynamic colors
        val preferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val useDynamicColors = preferences.getBoolean("use_dynamic_colors", true)

        setContent {
            // Get reference to repository from application class
            val repository = (application as MyMoneyNotesApplication).repository

            // Create ViewModel using factory
            val transactionViewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(repository)
            )

            MyMoneyNotesApp(useDynamicColors, transactionViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoneyNotesApp(
    useDynamicColors: Boolean = true,
    viewModel: TransactionViewModel
) {
    MyMoneyNotesAppTheme(
        dynamicColor = useDynamicColors
    ) {
        val navController = rememberNavController()

        // 🔥 ADD THIS: Tracking the current page
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = currentBackStackEntry?.destination?.route

        // You can use `currentRoute` anywhere now!
        LaunchedEffect(currentRoute) {
            currentRoute?.let {
                // Log it or send it to analytics
                println("Now viewing page: $it")
            }
        }

        // Collect state from ViewModel
        val transactions by viewModel.allTransactions.collectAsState()
        val financialSummary by viewModel.financialSummary.collectAsState()


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MyMoney Notes") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "add_transaction",
                        onClick = { navController.navigate("add_transaction") },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add") },
                        label = { Text("Add") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "stats",
                        onClick = { navController.navigate("stats") },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Info, contentDescription = "Stats") },
                        label = { Text("Stats") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(
                        transactions = transactions,
                        financialSummary = financialSummary,
                        onDeleteTransaction = { transaction ->
                            viewModel.deleteTransaction(transaction)
                        }
                    )
                }
                composable("add_transaction") {
                    AddTransactionScreen(
                        onTransactionAdded = { transaction ->
                            viewModel.addTransaction(transaction)
                            navController.navigate("home")
                        }
                    )
                }
                composable("stats") {
                    StatsScreen(transactions = transactions)
                }
            }
        }
    }
}