package com.vti.mcproject.data.repository

import com.vti.mcproject.data.model.Transaction

class TransactionRepository {
    fun getAllTransactions(): List<Transaction?> {
        return listOf(
            Transaction(
                "abc123",
                "erd1sender",
                "erd1receiver",
                "100.5",
                1699000000L,
                "success",
                "0.001",
                "",
                50000L,
                60000L
            ),
            Transaction(
                "xyz987",
                "erd1alice",
                "erd1bob",
                "50.25",
                1698900000L,
                "pending",
                "0.0005",
                "",
                30000L,
                40000L
            ),
            Transaction(
                "mno456",
                "erd1charlie",
                "erd1dave",
                "25.0",
                1698800000L,
                "failed",
                "0.0008",
                "",
                45000L,
                50000L
            )
        )
    }
}