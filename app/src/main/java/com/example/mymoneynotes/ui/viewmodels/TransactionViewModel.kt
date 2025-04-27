package com.example.mymoneynotes.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel responsible for managing transaction data and financial analytics
 */
class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // Stream of all transactions, sorted by date (newest first)
    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .map { transactions -> transactions.sortedByDescending { it.date } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = emptyList()
        )

    // Financial summary combining income, expense and balance
    val financialSummary: StateFlow<FinancialSummary> = combine(
        repository.totalIncome,
        repository.totalExpense
    ) { income, expense ->
        FinancialSummary(
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = FinancialSummary(0.0, 0.0, 0.0)
    )

    // Helper for stats screen to filter transactions by type
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        repository.allTransactions
            .map { transactions -> transactions.filter { it.type == type } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = emptyList()
            )

    // Returns transactions from the last 30 days
    val recentTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .map { transactions ->
            val thirtyDaysAgo = Date(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000))
            transactions
                .filter { it.date.after(thirtyDaysAgo) }
                .sortedByDescending { it.date }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = emptyList()
        )

    // Insert a new transaction
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    // Delete a transaction
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    /**
     * Data class representing financial summary metrics
     */
    data class FinancialSummary(
        val totalIncome: Double,
        val totalExpense: Double,
        val balance: Double
    ) {
        val incomePercentage: Float get() =
            if (totalIncome + totalExpense > 0) (totalIncome / (totalIncome + totalExpense)).toFloat() else 0f

        val expensePercentage: Float get() =
            if (totalIncome + totalExpense > 0) (totalExpense / (totalIncome + totalExpense)).toFloat() else 0f
    }
}

/**
 * Factory for creating TransactionViewModel with its dependencies
 */
class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}