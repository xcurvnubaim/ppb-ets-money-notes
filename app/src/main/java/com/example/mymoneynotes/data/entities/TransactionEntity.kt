package com.example.mymoneynotes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mymoneynotes.data.TransactionCategory
import com.example.mymoneynotes.data.TransactionType
import java.util.Date

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String, // Storing as String since Room doesn't directly support Enums
    val category: String,
    val amount: Double,
    val description: String,
    val timestamp: Long // Store date as timestamp for Room
) {
    // Convert from domain model to entity
    companion object {
        fun fromTransaction(transaction: com.example.mymoneynotes.data.Transaction): TransactionEntity {
            return TransactionEntity(
                id = transaction.id,
                type = transaction.type.name,
                category = transaction.category.name,
                amount = transaction.amount,
                description = transaction.description,
                timestamp = transaction.date.time
            )
        }
    }

    // Convert to domain model
    fun toTransaction(): com.example.mymoneynotes.data.Transaction {
        return com.example.mymoneynotes.data.Transaction(
            id = id,
            type = TransactionType.valueOf(type),
            category = TransactionCategory.valueOf(category),
            amount = amount,
            description = description,
            date = Date(timestamp)
        )
    }
}