package com.example.mymoneynotes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.ui.viewmodels.TransactionViewModel.FinancialSummary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    financialSummary: FinancialSummary,
    onDeleteTransaction: (Transaction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Balance",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Rp ${String.format("%,.0f", financialSummary.balance)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (financialSummary.balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Rp ${String.format("%,.0f", financialSummary.totalIncome)}",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Rp ${String.format("%,.0f", financialSummary.totalExpense)}",
                            color = Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        onDelete = { onDeleteTransaction(transaction) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = dateFormatter.format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} Rp ${String.format("%,.0f", transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}