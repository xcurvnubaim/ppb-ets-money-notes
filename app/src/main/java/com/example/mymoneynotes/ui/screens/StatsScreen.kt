package com.example.mymoneynotes.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionCategory
import com.example.mymoneynotes.data.TransactionType
import kotlin.math.min

@Composable
fun StatsScreen(transactions: List<Transaction>) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    val scrollState = rememberScrollState()

    // Animation for type selection
    val animatedSelectedType = remember { mutableStateOf(selectedType) }
    LaunchedEffect(selectedType) {
        animatedSelectedType.value = selectedType
    }

    val filteredTransactions = transactions.filter { it.type == selectedType }
    val totalAmount = filteredTransactions.sumOf { it.amount }

    val categorySums = TransactionCategory.values().associateWith { category ->
        filteredTransactions
            .filter { it.category == category }
            .sumOf { it.amount }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Financial Statistics",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Fixed toggle between Income and Expense (no white line)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp) // Fixed height for consistent appearance
                ) {
                    TransactionTypeSegment(
                        text = "Expenses",
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = { selectedType = TransactionType.EXPENSE },
                        modifier = Modifier.weight(1f)
                    )
                    TransactionTypeSegment(
                        text = "Income",
                        selected = selectedType == TransactionType.INCOME,
                        onClick = { selectedType = TransactionType.INCOME },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                EmptyState(selectedType = selectedType)
            } else {
                // Pie Chart Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(bottom = 16.dp)
                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PieChart(
                            data = categorySums.filter { it.value > 0 },
                            totalAmount = totalAmount
                        )
                    }
                }

                // Category Breakdown Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "${selectedType.name.lowercase().capitalize()} Breakdown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        categorySums
                            .filter { it.value > 0 }
                            .toList()
                            .sortedByDescending { it.second }
                            .forEach { (category, amount) ->
                                val percentage = (amount / totalAmount * 100)
                                // Animate the progress
                                val animatedProgress by animateFloatAsState(
                                    targetValue = (amount / totalAmount).toFloat(),
                                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                                )

                                CategoryBreakdownItem(
                                    category = category,
                                    amount = amount,
                                    percentage = percentage,
                                    progress = animatedProgress
                                )

                                if (categorySums.filter { it.value > 0 }.toList().sortedByDescending { it.second }.last().first != category) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(selectedType: TransactionType) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "¯\\_(ツ)_/¯",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No ${selectedType.name.lowercase()} transactions to display",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryBreakdownItem(
    category: TransactionCategory,
    amount: Double,
    percentage: Double,
    progress: Float
) {
    val categoryColor = getCategoryColor(category)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(categoryColor)
                        .padding(end = 8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.name.lowercase().capitalize(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "Rp ${String.format("%,.0f", amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom progress indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(categoryColor)
            )
        }

        Text(
            text = String.format("%.1f%%", percentage),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionTypeSegment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300)
    )

    // Removed padding to fix white line issue
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@SuppressLint("DefaultLocale")
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
            .padding(24.dp)) {

            val canvasWidth = size.width
            val canvasHeight = size.height
            val radius = min(canvasWidth, canvasHeight) / 2f
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // Animation for pie chart
            var startAngle = 0f

            // Draw shadow for chart
            drawCircle(
                color = Color.Black.copy(alpha = 0.1f),
                radius = radius + 4f,
                center = center.copy(x = center.x + 2f, y = center.y + 2f)
            )

            data.forEach { (category, amount) ->
                val sweepAngle = (amount / totalAmount * 360f).toFloat()

                // Draw slice with slight padding
                drawArc(
                    color = getCategoryColor(category),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 1f, // Small gap between slices
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                // Draw highlight border
                drawArc(
                    color = Color.White.copy(alpha = 0.3f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle - 1f,
                    useCenter = true,
                    style = Stroke(width = 2f),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                startAngle += sweepAngle
            }

            // Draw inner circle for donut chart effect with subtle shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.05f),
                radius = radius * 0.6f + 2f,
                center = center.copy(x = center.x + 1f, y = center.y + 1f)
            )

            drawCircle(
                color = colorScheme.surface,
                radius = radius * 0.6f,
                center = center
            )
        }

        // Center content with adaptive text size
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Dynamic text size based on amount length
            val totalText = "Rp ${String.format("%,.0f", totalAmount)}"
            val textSize = when {
                totalText.length > 15 -> 18.sp
                totalText.length > 12 -> 20.sp
                totalText.length > 9 -> 24.sp
                else -> 28.sp
            }

            Text(
                text = totalText,
                fontSize = textSize,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
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

// Extension function to capitalize the first letter
fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this.first().uppercase() + this.substring(1)
    } else {
        this
    }
}