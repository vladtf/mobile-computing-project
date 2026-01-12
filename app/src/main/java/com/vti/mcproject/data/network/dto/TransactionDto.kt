package com.vti.mcproject.data.network.dto

import com.vti.mcproject.data.model.Transaction
import com.vti.mcproject.utils.CurrencyUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    @SerialName("txHash")
    val txHash: String? = null,
    @SerialName("originalTxHash")
    val originalTxHash: String? = null,
    @SerialName("sender")
    val sender: String = "",
    @SerialName("receiver")
    val receiver: String = "",
    @SerialName("value")
    val value: String = "0",
    @SerialName("timestamp")
    val timestamp: Long = 0L,
    @SerialName("status")
    val status: String = "unknown",
    @SerialName("fee")
    val fee: String? = null,
    @SerialName("data")
    val data: String? = null,
    @SerialName("gasUsed")
    val gasUsed: Long? = null,
    @SerialName("gasLimit")
    val gasLimit: Long? = null
) {
    fun getEffectiveHash(): String = txHash ?: originalTxHash ?: ""

    fun toDomain(): Transaction = Transaction(
        hash = getEffectiveHash(),
        sender = sender,
        receiver = receiver,
        value = CurrencyUtils.convertWeiToEgld(value),
        timestamp = timestamp,
        status = status,
        fee = fee?.let { CurrencyUtils.convertWeiToEgld(it) } ?: "0",
        data = data,
        gasUsed = gasUsed ?: 0L,
        gasLimit = gasLimit ?: 0L
    )
}
