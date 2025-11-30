package com.example.codechecker.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for cryptographic operations
 */
@Singleton
class CryptoUtils @Inject constructor() {

    /**
     * Generate SHA-256 hash of input string
     */
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generate MD5 hash of input string
     */
    fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Encrypt string using AES
     */
    fun encrypt(plaintext: String, key: String): String {
        try {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = IvParameterSpec(ByteArray(16))
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
            val encrypted = cipher.doFinal(plaintext.toByteArray())
            return Base64.encodeToString(encrypted, Base64.DEFAULT).trim()
        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }

    /**
     * Decrypt string using AES
     */
    fun decrypt(ciphertext: String, key: String): String {
        try {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = IvParameterSpec(ByteArray(16))
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
            val decoded = Base64.decode(ciphertext, Base64.DEFAULT)
            val decrypted = cipher.doFinal(decoded)
            return String(decrypted)
        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }

    /**
     * Generate random password hash with salt
     */
    fun hashPassword(password: String): String {
        val salt = System.currentTimeMillis().toString()
        return sha256(password + salt)
    }
}
