package com.vti.mcproject.data.repository

import com.vti.mcproject.data.network.MultiversXService
import com.vti.mcproject.data.model.AccountInfo

class AccountInfoRepository {
    
    private val multiversXService = MultiversXService()
    
    suspend fun getAccountInfo(address: String = MultiversXService.CONTRACT_ADDRESS): Result<AccountInfo> {
        return multiversXService.getAccountInfo(address)
    }
}