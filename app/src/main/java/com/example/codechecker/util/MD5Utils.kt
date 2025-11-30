package com.example.codechecker.util

import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for MD5 hashing operations
 */
@Singleton
class MD5Utils @Inject constructor() {

    /**
     * Calculate MD5 hash of input string
     */
    fun calculateMD5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return bytesToHex(digest)
    }

    /**
     * Calculate MD5 hash of byte array
     */
    fun calculateMD5(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(bytes)
        return bytesToHex(digest)
    }

    /**
     * Convert byte array to hex string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val b = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = hexChars[b shr 4]
            hexChars[i * 2 + 1] = hexChars[b and 0x0F]
        }
        return String(hexChars)
    }

    /**
     * Verify if input matches expected MD5 hash
     */
    fun verifyMD5(input: String, expectedHash: String): Boolean {
        val calculatedHash = calculateMD5(input)
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }
}
