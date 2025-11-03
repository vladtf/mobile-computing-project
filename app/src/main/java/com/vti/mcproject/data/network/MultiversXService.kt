package com.vti.mcproject.data.network

import android.util.Log
import com.elrond.erdkotlin.ElrondNetwork
import com.elrond.erdkotlin.ErdSdk
import com.elrond.erdkotlin.domain.wallet.models.Address
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.utils.CurrencyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Service for interacting with MultiversX blockchain API
 */
class MultiversXService(
    private val network: ElrondNetwork = ElrondNetwork.DevNet
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    init {
        ErdSdk.setNetwork(network)
    }

    /**
     * Get account information for a given address
     */
    suspend fun getAccountInfo(addressBech32: String): Result<AccountInfo> = withContext(Dispatchers.IO) {
        try {
            val address = Address.fromBech32(addressBech32)
            val account = ErdSdk.getAccountUsecase().execute(address)
            
            Result.success(
                AccountInfo(
                    address = addressBech32,
                    balance = CurrencyUtils.convertWeiToEgld(account.balance.toString()),
                    nonce = account.nonce
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching account info: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get transactions for a given address
     */
    suspend fun getTransactions(addressBech32: String): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            Address.fromBech32(addressBech32) // Validate address
            
            val url = "${network.url()}/accounts/$addressBech32/transfers?from=0&size=25"
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("API request failed: ${response.code}")
                )
            }
            
            val jsonString = response.body?.string() ?: return@withContext Result.success(emptyList())
            Result.success(Transaction.parseFromJson(jsonString))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transactions: ${e.message}", e)
            Result.success(emptyList())
        }
    }

    companion object {
        private const val TAG = "MultiversXService"
        const val CONTRACT_ADDRESS = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma"
    }
}
