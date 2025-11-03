package com.vti.mcproject.data.repository

import com.vti.mcproject.blockchain.MultiversXSdkService
import com.vti.mcproject.data.model.AccountInfo

class AccountInfoRepository {
    
    private val multiversXService = MultiversXSdkService()
    
    suspend fun getAccountInfo(address: String = MultiversXSdkService.CONTRACT_ADDRESS): Result<AccountInfo> {
        return multiversXService.getAccountInfo(address)
    }
}