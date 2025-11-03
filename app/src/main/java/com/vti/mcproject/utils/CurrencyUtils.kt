package com.vti.mcproject.utils

import java.math.BigInteger

/**
 * Utility functions for currency conversion
 */
object CurrencyUtils {
    
    /**
     * Convert wei (smallest unit) to EGLD
     */
    fun convertWeiToEgld(weiAmount: String): String {
        return try {
            val wei = BigInteger(weiAmount)
            val egld = wei.toBigDecimal().divide(BigInteger.TEN.pow(18).toBigDecimal())
            egld.stripTrailingZeros().toPlainString()
        } catch (e: Exception) {
            "0"
        }
    }
    
    /**
     * Convert EGLD to wei (smallest unit)
     */
    fun convertEgldToWei(egldAmount: String): String {
        return try {
            val egld = egldAmount.toBigDecimal()
            val wei = egld.multiply(BigInteger.TEN.pow(18).toBigDecimal())
            wei.toBigInteger().toString()
        } catch (e: Exception) {
            "0"
        }
    }
}
