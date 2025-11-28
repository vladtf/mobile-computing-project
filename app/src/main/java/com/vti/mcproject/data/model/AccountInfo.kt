package com.vti.mcproject.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_info")
data class AccountInfo(
    @PrimaryKey
    val address: String,
    val balance: String,
    val nonce: Long
)
