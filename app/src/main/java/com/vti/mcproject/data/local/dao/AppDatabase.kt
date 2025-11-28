package com.vti.mcproject.data.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction

@Database(entities = [Transaction::class, AccountInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountInfoDao(): AccountInfoDao

    companion object {
        const val DATABASE_NAME = "mcproject_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                return INSTANCE ?: databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                    .also { INSTANCE = it }
            }
        }
    }

}