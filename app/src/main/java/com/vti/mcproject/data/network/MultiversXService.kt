package com.vti.mcproject.data.network

import android.util.Log
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.utils.CurrencyUtils
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Service for interacting with MultiversX blockchain API.
 */
class MultiversXService(
    baseUrl: String = BASE_URL
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
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: MultiversXApi = retrofit.create(MultiversXApi::class.java)

    /**
     * Get account information for a given address using Retrofit.
     */
    suspend fun getAccountInfo(addressBech32: String): Result<AccountInfo> {
        return try {
            val dto = api.getAccount(addressBech32)
            Result.success(
                AccountInfo(
                    address = dto.address,
                    balance = CurrencyUtils.convertWeiToEgld(dto.balance),
                    nonce = dto.nonce
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching account info: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getTransactions(addressBech32: String): Result<List<Transaction>> {
        return try {
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
        private const val BASE_URL = "https://devnet-api.multiversx.com/"
        const val CONTRACT_ADDRESS = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma"
    }
}