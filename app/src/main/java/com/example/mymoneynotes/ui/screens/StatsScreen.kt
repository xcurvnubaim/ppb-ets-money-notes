package com.example.mymoneynotes.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionCategory
import com.example.mymoneynotes.data.TransactionType
import kotlin.math.min

@Composable
fun StatsScreen(transactions: List<Transaction>) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val filteredTransactions = transactions.filter { it.type == selectedType }

    val totalAmount = filteredTransactions.sumOf { it.amount }

    val categorySums = TransactionCategory.values().associateWith { category ->
        filteredTransactions
            .filter { it.category == category }
            .sumOf { it.amount }
    }

    val scrollState = rememberScrollState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Financial Statistics",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Toggle between Income and Expense
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionTypeButton(
                text = "Expenses",
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { selectedType = TransactionType.EXPENSE },
                modifier = Modifier.weight(1f)
            )
            TransactionTypeButton(
                text = "Income",
                selected = selectedType == TransactionType.INCOME,
                onClick = { selectedType = TransactionType.INCOME },
                modifier = Modifier.weight(1f)
            )
        }

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No ${selectedType.name.lowercase()} transactions to display",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            // Pie Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    data = categorySums.filter { it.value > 0 },
                    totalAmount = totalAmount
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Breakdown
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${selectedType.name} Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Divider()

                    categorySums
                        .filter { it.value > 0 }
                        .toList()
                        .sortedByDescending { it.second }
                        .forEach { (category, amount) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(end = 8.dp)
                                    ) {
                                        Canvas(modifier = Modifier.size(16.dp)) {
                                            drawCircle(color = getCategoryColor(category))
                                        }
                                    }
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = "Rp ${String.format("%,.0f", amount)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val percentage = (amount / totalAmount * 100)
                            LinearProgressIndicator(
                                progress = (amount / totalAmount).toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = getCategoryColor(category)
                            )

                            Text(
                                text = String.format("%.1f%%", percentage),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                textAlign = TextAlign.End
                            )

                            if (categorySums.filter { it.value > 0 }.toList().sortedByDescending { it.second }.last().first != category) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: Map<TransactionCategory, Double>,
    totalAmount: Double
) {
    if (data.isEmpty()) return
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = min(canvasWidth, canvasHeight) / 2f
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

            var startAngle = 0f

            data.forEach { (category, amount) ->
                val sweepAngle = (amount / totalAmount * 360f).toFloat()

                // Draw slice
                drawArc(
                    color = getCategoryColor(category),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Draw border
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    style = Stroke(width = 2f),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                startAngle += sweepAngle
            }

            // Draw inner circle for donut chart effect
            drawCircle(
                color = colorScheme.surface,
                radius = radius * 0.6f,
                center = center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Rp ${String.format("%,.0f", totalAmount)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getCategoryColor(category: TransactionCategory): Color {
    return when (category) {
        TransactionCategory.FOOD -> Color(0xFFFF5252)
        TransactionCategory.TRANSPORT -> Color(0xFF448AFF)
        TransactionCategory.ENTERTAINMENT -> Color(0xFFAB47BC)
        TransactionCategory.SHOPPING -> Color(0xFF66BB6A)
        TransactionCategory.BILLS -> Color(0xFFFFB300)
        TransactionCategory.SALARY -> Color(0xFF26C6DA)
        TransactionCategory.GIFT -> Color(0xFFEC407A)
        TransactionCategory.OTHER -> Color(0xFF78909C)
    }
}
