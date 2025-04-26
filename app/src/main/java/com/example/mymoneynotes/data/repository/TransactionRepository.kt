package com.example.mymoneynotes.data.repository

import com.example.mymoneynotes.data.Transaction
import com.example.mymoneynotes.data.TransactionType
import com.example.mymoneynotes.data.dao.TransactionDao
import com.example.mymoneynotes.data.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
        .map { entities -> entities.map { it.toTransaction() } }

    val incomeTransactions: Flow<List<Transaction>> = transactionDao.getTransactionsByType(TransactionType.INCOME.name)
        .map { entities -> entities.map { it.toTransaction() } }

    val expenseTransactions: Flow<List<Transaction>> = transactionDao.getTransactionsByType(TransactionType.EXPENSE.name)
        .map { entities -> entities.map { it.toTransaction() } }

    val totalIncome: Flow<Double> = transactionDao.getTotalIncome()
        .map { it ?: 0.0 }

    val totalExpense: Flow<Double> = transactionDao.getTotalExpense()
        .map { it ?: 0.0 }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(TransactionEntity.fromTransaction(transaction))
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(TransactionEntity.fromTransaction(transaction))
    }
}