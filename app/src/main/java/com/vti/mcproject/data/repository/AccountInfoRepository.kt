package com.vti.mcproject.data.repository

import com.vti.mcproject.data.model.AccountInfo

class AccountInfoRepository {
    fun getAccountInfo(): AccountInfo {
        return AccountInfo(
            address = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma",
            balance = "1234.567",
            nonce = 42
        )
    }
}