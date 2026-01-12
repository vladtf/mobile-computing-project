package com.vti.mcproject.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    @SerialName("address")
    val address: String = "",
    @SerialName("balance")
    val balance: String = "0",
    @SerialName("nonce")
    val nonce: Long = 0L,
    @SerialName("username")
    val username: String? = null,
    @SerialName("shard")
    val shard: Int? = null
)
