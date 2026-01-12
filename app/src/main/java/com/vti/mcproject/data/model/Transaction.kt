package com.vti.mcproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
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