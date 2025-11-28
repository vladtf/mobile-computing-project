package com.vti.mcproject.data.local.dao

import android.content.Context
import com.vti.mcproject.data.repository.AccountInfoRepository
import com.vti.mcproject.data.repository.TransactionRepository

interface AppContainer {
    val transactionRepository: TransactionRepository
    val accountInfoRepository: AccountInfoRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(
            transactionDao = AppDatabase.getInstance(context).transactionDao()
        )
    }

    override val accountInfoRepository: AccountInfoRepository by lazy {
        AccountInfoRepository(
            accountInfoDao = AppDatabase.getInstance(context).accountInfoDao()
        )
    }
}
