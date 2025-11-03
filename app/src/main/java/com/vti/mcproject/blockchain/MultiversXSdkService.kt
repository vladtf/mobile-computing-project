package com.vti.mcproject.blockchain

import android.util.Log
import com.elrond.erdkotlin.ElrondNetwork
import com.elrond.erdkotlin.ErdSdk
import com.elrond.erdkotlin.domain.wallet.models.Address
import com.vti.mcproject.data.model.AccountInfo
import com.vti.mcproject.data.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.math.BigInteger
import java.util.concurrent.TimeUnit

/**
 * Service for interacting with MultiversX blockchain using erdkotlin SDK
 */
class MultiversXSdkService(
    private val network: ElrondNetwork = ElrondNetwork.DevNet
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "HTTP Request: ${request.method} ${request.url}")
            Log.d(TAG, "Headers: ${request.headers}")
            
            val response = chain.proceed(request)
            Log.d(TAG, "HTTP Response: ${response.code} ${response.message}")
            
            response
        }
        .build()

    init {
        // Set the network for the SDK
        ErdSdk.setNetwork(network)
        Log.d(TAG, "Initialized with network: ${network.url()}")
    }

    /**
     * Data class for transaction information
     */


    /**
     * Data class for account information
     */

    /**
     * Get account information for a given address
     */
    suspend fun getAccountInfo(addressBech32: String): Result<AccountInfo> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching account info for: $addressBech32")
            
            val address = Address.fromBech32(addressBech32)
            Log.d(TAG, "Address parsed: ${address.bech32()}")
            
            val account = ErdSdk.getAccountUsecase().execute(address)
            
            val balanceInEgld = convertWeiToEgld(account.balance.toString())
            
            Log.d(TAG, "Account info fetched - Balance: $balanceInEgld, Nonce: ${account.nonce}")
            
            Result.success(
                AccountInfo(
                    address = addressBech32,
                    balance = balanceInEgld,
                    nonce = account.nonce
                )
            )
        } catch (e: com.elrond.erdkotlin.Exceptions.ProxyRequestException) {
            Log.e(TAG, "API Error fetching account info: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching account info", e)
            Result.failure(e)
        }
    }

    /**
     * Get transactions for a given address using direct API call
     * Using /accounts/{address}/transfers endpoint as the SDK uses outdated endpoint
     */
    suspend fun getTransactions(addressBech32: String): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching transactions for: $addressBech32")
            
            // Validate address
            val address = Address.fromBech32(addressBech32)
            Log.d(TAG, "Address parsed successfully: ${address.bech32()}")
            
            // Use the correct API endpoint: /accounts/{address}/transfers
            val url = "${network.url()}/accounts/${addressBech32}/transfers?from=0&size=25"
            Log.d(TAG, "API URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .get()
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e(TAG, "API request failed: ${response.code} - ${response.message}")
                return@withContext Result.failure(
                    Exception("API request failed: ${response.code} - ${response.message}")
                )
            }
            
            val jsonString = response.body?.string() ?: return@withContext Result.success(emptyList())
            Log.d(TAG, "API Response received, parsing...")
            
            val txList = parseTransfersFromJson(jsonString)
            Result.success(txList)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transactions", e)
            Result.success(emptyList()) // Return empty list instead of error for better UX
        }
    }
    
    /**
     * Parse transactions from the /transfers API response
     */
    private fun parseTransfersFromJson(jsonString: String): List<Transaction> {
        try {
            val jsonArray = JSONArray(jsonString)
            val transactions = mutableListOf<Transaction>()
            
            for (i in 0 until jsonArray.length()) {
                val txJson = jsonArray.getJSONObject(i)
                
                val value = txJson.optString("value", "0")
                val txHash = txJson.optString("txHash", txJson.optString("originalTxHash", ""))
                val sender = txJson.optString("sender", "")
                val receiver = txJson.optString("receiver", "")
                val timestamp = txJson.optLong("timestamp", 0L)
                val status = txJson.optString("status", "unknown")
                
                // Note: transfers API doesn't provide fee and gasUsed in the same way
                val transaction = Transaction(
                    hash = txHash,
                    sender = sender,
                    receiver = receiver,
                    value = convertWeiToEgld(value),
                    timestamp = timestamp,
                    status = status,
                    fee = "0", // Not available in transfers endpoint
                    data = "", // Not available in transfers endpoint
                    gasUsed = 0L,
                    gasLimit = 0L
                )
                
                transactions.add(transaction)
            }
            
            return transactions
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing transactions", e)
            return emptyList()
        }
    }

    /**
     * Get specific transaction information by hash
     */
    suspend fun getTransactionInfo(txHash: String): Result<Transaction?> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching transaction info for hash: $txHash")
            
            val txInfo = ErdSdk.getTransactionInfoUsecase().execute(txHash, null)
            
            val transaction = Transaction(
                hash = txHash, // Use the hash we queried with
                sender = txInfo.sender.bech32(),
                receiver = txInfo.receiver.bech32(),
                value = convertWeiToEgld(txInfo.value.toString()),
                timestamp = 0L, // Not available in TransactionInfo
                status = txInfo.status,
                fee = "0", // Not available in TransactionInfo
                data = txInfo.data ?: "",
                gasUsed = 0L, // Not available in TransactionInfo
                gasLimit = txInfo.gasLimit
            )
            
            Result.success(transaction)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transaction info", e)
            Result.failure(e)
        }
    }

    /**
     * Get transaction status
     */
    suspend fun getTransactionStatus(txHash: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching transaction status for hash: $txHash")
            
            val status = ErdSdk.getTransactionStatusUsecase().execute(txHash, null)
            
            Result.success(status)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transaction status", e)
            Result.failure(e)
        }
    }

    /**
     * Query smart contract
     */
    suspend fun queryContract(
        contractAddress: String,
        functionName: String,
        args: List<String> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Querying contract: $contractAddress, function: $functionName")
            
            val address = Address.fromBech32(contractAddress)
            val result = ErdSdk.queryContractStringUsecase().execute(
                contractAddress = address,
                funcName = functionName,
                args = args,
                caller = null,
                value = null
            )
            
            Result.success(result.data)
        } catch (e: Exception) {
            Log.e(TAG, "Error querying contract", e)
            Result.failure(e)
        }
    }

    /**
     * Convert wei (smallest unit) to EGLD
     */
    private fun convertWeiToEgld(weiAmount: String): String {
        return try {
            val wei = BigInteger(weiAmount)
            val egld = wei.toBigDecimal().divide(BigInteger.TEN.pow(18).toBigDecimal())
            egld.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            "0"
        }
    }

    companion object {
        const val TAG = "MultiversXSdkService"
        const val CONTRACT_ADDRESS = "erd1qqqqqqqqqqqqqpgquvpnteagc5xsslc3yc9hf6um6n6jjgzdd8ss07v9ma"
    }
}
