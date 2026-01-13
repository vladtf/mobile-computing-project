package com.vti.mcproject.utils

/**
 * Utility functions for address formatting
 */
object AddressUtils {
    
    /**
     * Shorten blockchain address for display purposes
     * @param address The full blockchain address
     * @return Shortened address in format "abc123...def456"
     */
    fun shortenAddress(address: String): String {
        return if (address.length > 13) {
            "${address.take(6)}...${address.takeLast(4)}"
        } else {
            address
        }
    }
}