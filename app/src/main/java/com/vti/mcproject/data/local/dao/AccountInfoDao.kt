package com.vti.mcproject.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vti.mcproject.data.model.AccountInfo
import kotlinx.coroutines.flow.Flow


@Dao
interface AccountInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(accountInfo: AccountInfo)

    @Update
    suspend fun update(accountInfo: AccountInfo)

    @Delete
    suspend fun delete(accountInfo: AccountInfo)

    @Query("DELETE FROM account_info WHERE address = :address")
    suspend fun deleteByAddress(address: String)

    @Query("DELETE FROM account_info")
    suspend fun deleteAll()

    @Query("SELECT * FROM account_info WHERE address = :address")
    fun getByAddress(address: String): Flow<AccountInfo?>

    @Query("SELECT * FROM account_info")
    fun getAll(): Flow<List<AccountInfo>>
}