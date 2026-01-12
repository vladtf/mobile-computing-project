package com.vti.mcproject.data.network

import android.util.Log
import com.elrond.erdkotlin.ElrondNetwork
import com.elrond.erdkotlin.ErdSdk
import com.elrond.erdkotlin.domain.wallet.models.Address
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.data.network.dto.TransactionDto
import com.vti.mcproject.utils.CurrencyUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * Service for interacting with MultiversX blockchain API
 */
class MultiversXService(
    private val network: ElrondNetwork = ElrondNetwork.DevNet
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(network.url() + "/")
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: MultiversXApi = retrofit.create(MultiversXApi::class.java)

    init {
        ErdSdk.setNetwork(network)
    }

    /**
     * Get account information for a given address using suspendCancellableCoroutine
     */
    suspend fun getAccountInfo(addressBech32: String): Result<AccountInfo> = 
        suspendCancellableCoroutine { continuation ->
            try {
                val address = Address.fromBech32(addressBech32)
                val account = ErdSdk.getAccountUsecase().execute(address)
                
                continuation.resume(
                    Result.success(
                        AccountInfo(
                            address = addressBech32,
                            balance = CurrencyUtils.convertWeiToEgld(account.balance.toString()),
                            nonce = account.nonce
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching account info: ${e.message}", e)
                continuation.resume(Result.failure(e))
            }
        }

    suspend fun getTransactions(addressBech32: String): Result<List<Transaction>> {
        return try {
            Address.fromBech32(addressBech32) // Validate address
            
            val dtos = api.getTransfers(addressBech32)
            val transactions = dtos.map { it.toDomain() }
            
            Result.success(transactions)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transactions: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "MultiversXService"
        const val CONTRACT_ADDRESS = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma"
    }
}