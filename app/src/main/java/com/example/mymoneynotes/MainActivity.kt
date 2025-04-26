package com.example.mymoneynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.ui.screens.*
import com.example.mymoneynotes.ui.theme.MyMoneyNotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMoneyNotesApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMoneyNotesApp() {
    // Shared ViewModel state
    val transactions = remember { mutableStateListOf<Transaction>() }

    MyMoneyNotesAppTheme {
        val navController = rememberNavController()

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
                        selected = true,
                        onClick = { navController.navigate("home") },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("add_transaction") },
                        icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Add") },
                        label = { Text("Add") }
                    )
                    NavigationBarItem(
                        selected = false,
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
                    HomeScreen(transactions = transactions)
                }
                composable("add_transaction") {
                    AddTransactionScreen(
                        onTransactionAdded = { transaction ->
                            transactions.add(transaction)
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