package com.vti.mcproject.data.network

import com.vti.mcproject.data.network.dto.AccountDto
import com.vti.mcproject.data.network.dto.TransactionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for MultiversX blockchain API
 */
interface MultiversXApi {

    /**
     * Get account information for a given address
     */
    @GET("accounts/{address}")
    suspend fun getAccount(
        @Path("address") address: String
    ): AccountDto

    /**
     * Get transactions/transfers for a given address
     */
    @GET("accounts/{address}/transfers")
    suspend fun getTransfers(
        @Path("address") address: String,
        @Query("from") from: Int = 0,
        @Query("size") size: Int = 25
    ): List<TransactionDto>
}
