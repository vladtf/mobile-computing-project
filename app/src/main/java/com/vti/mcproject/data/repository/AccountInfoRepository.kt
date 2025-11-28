package com.vti.mcproject.data.repository

import com.vti.mcproject.data.local.dao.AccountInfoDao
import com.vti.mcproject.data.network.MultiversXService
import com.vti.mcproject.data.model.AccountInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountInfoRepository(
    private val multiversXService: MultiversXService = MultiversXService(),
    private val accountInfoDao: AccountInfoDao
) {

    fun observeAccountInfo(): Flow<AccountInfo?> {
        return accountInfoDao.getAll().map { it.firstOrNull() }
    }

    suspend fun getAccountInfo(address: String = MultiversXService.CONTRACT_ADDRESS): Result<AccountInfo> {
        return multiversXService.getAccountInfo(address)
            .also { result ->
                result.onSuccess { accountInfo ->
                    accountInfoDao.insert(accountInfo)
                }
            }
    }
}