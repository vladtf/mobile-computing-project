package com.vti.mcproject.data.network

import com.vti.mcproject.data.network.dto.AccountDto
import com.vti.mcproject.data.network.dto.TransactionDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MultiversXApi {

    @GET("accounts/{address}")
    suspend fun getAccount(
        @Path("address") address: String
    ): AccountDto

    @GET("accounts/{address}/transfers")
    suspend fun getTransfers(
        @Path("address") address: String,
        @Query("from") from: Int = 0,
        @Query("size") size: Int = 25
    ): List<TransactionDto>
}
