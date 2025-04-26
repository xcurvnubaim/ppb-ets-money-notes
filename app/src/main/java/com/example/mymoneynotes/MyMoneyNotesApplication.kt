package com.example.mymoneynotes

import android.app.Application
import com.example.mymoneynotes.data.database.AppDatabase
import com.example.mymoneynotes.data.repository.TransactionRepository

class MyMoneyNotesApplication : Application() {

    // Initialize database and repository
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}