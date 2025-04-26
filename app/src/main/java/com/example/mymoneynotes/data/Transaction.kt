package com.example.mymoneynotes.data

import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory {
    FOOD, TRANSPORT, ENTERTAINMENT, SHOPPING, BILLS, SALARY, GIFT, OTHER
}

data class Transaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val description: String,
    val date: Date = Date()
)