package com.vti.mcproject.data.model

data class Transaction(
    val hash: String,
    val sender: String,
    val receiver: String,
    val value: String,
    val timestamp: Long,
    val status: String,
    val fee: String,
    val data: String? = null,
    val gasUsed: Long = 0,
    val gasLimit: Long = 0
)