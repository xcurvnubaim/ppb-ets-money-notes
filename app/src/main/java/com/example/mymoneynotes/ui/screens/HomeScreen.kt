package com.example.mymoneynotes.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.ui.screens.getCategoryColor
import com.example.mymoneynotes.ui.viewmodels.TransactionViewModel.FinancialSummary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    transactions: List<Transaction>,
    financialSummary: FinancialSummary,
    onDeleteTransaction: (Transaction) -> Unit
) {
    val listState = rememberLazyListState()
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Balance Card with Surface and Elevation
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Current Balance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = currencyFormatter.format(financialSummary.balance).replace("IDR", "Rp"),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (financialSummary.balance >= 0) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Income and Expense Summary Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Income Card
                    SummaryCard(
                        title = "Income",
                        amount = financialSummary.totalIncome,
                        icon = Icons.Filled.ArrowUpward,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Divider
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    // Expense Card
                    SummaryCard(
                        title = "Expense",
                        amount = financialSummary.totalExpense,
                        icon = Icons.Filled.ArrowDownward,
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress Bar for Income/Expense Ratio
                if (financialSummary.totalIncome + financialSummary.totalExpense > 0) {
                    val incomeRatio = financialSummary.incomePercentage
                    
                    LinearProgressIndicator(
                        progress = incomeRatio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        trackColor = MaterialTheme.colorScheme.errorContainer,
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(incomeRatio * 100).toInt()}% Income",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(100 - incomeRatio * 100).toInt()}% Expense",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Transactions Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                AssistChip(
                    onClick = { /* TODO: Add filter functionality */ },
                    label = { Text("Last 30 days") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }

            AnimatedVisibility(
                visible = transactions.isEmpty(),
                enter = fadeIn(spring()),
                exit = fadeOut(spring())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No transactions yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first transaction using the + button",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = transactions.isNotEmpty(),
                enter = fadeIn(spring()),
                exit = fadeOut(spring())
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
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
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = currencyFormatter.format(amount).replace("IDR", "Rp"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category indicator and info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category color indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(transaction.category))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Here you could add a category icon if available
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormatter.format(transaction.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = timeFormatter.format(transaction.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount and delete button
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"} ${
                        currencyFormatter.format(transaction.amount).replace("IDR", "Rp")
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.type == TransactionType.INCOME) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
                
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete transaction",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this ${transaction.category.name} transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}