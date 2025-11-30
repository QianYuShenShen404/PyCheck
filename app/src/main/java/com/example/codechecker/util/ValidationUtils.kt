package com.example.codechecker.util

import javax.inject.Inject
import javax.inject.Singleton
import android.util.Patterns

/**
 * Utility class for validation operations
 */
@Singleton
class ValidationUtils @Inject constructor() {

    /**
     * Validate username
     */
    fun validateUsername(username: String): Boolean {
        // Username should be 3-20 characters, alphanumeric and underscore only
        val pattern = Regex("^[a-zA-Z0-9_]{3,20}$")
        return pattern.matches(username)
    }

    /**
     * Validate password
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult(false, "密码长度至少6位")
            password.length > 20 -> ValidationResult(false, "密码长度不能超过20位")
            !password.any { it.isUpperCase() } -> ValidationResult(false, "密码需包含大写字母")
            !password.any { it.isLowerCase() } -> ValidationResult(false, "密码需包含小写字母")
            !password.any { it.isDigit() } -> ValidationResult(false, "密码需包含数字")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Validate display name
     */
    fun validateDisplayName(displayName: String): Boolean {
        // Display name should be 2-30 characters
        return displayName.length in 2..30
    }

    /**
     * Validate email
     */
    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validate assignment title
     */
    fun validateAssignmentTitle(title: String): Boolean {
        return title.isNotBlank() && title.length <= 100
    }

    /**
     * Validate assignment description
     */
    fun validateAssignmentDescription(description: String): Boolean {
        return description.isNotBlank() && description.length <= 500
    }

    /**
     * Check if username is valid (for UI use)
     */
    fun isValidUsername(username: String): Boolean {
        return validateUsername(username)
    }

    /**
     * Check if password is valid (for UI use)
     */
    fun isValidPassword(password: String): Boolean {
        return validatePassword(password).isValid
    }

    /**
     * Check if filename is a valid Python file
     */
    fun isValidPythonFileName(fileName: String): Boolean {
        return fileName.endsWith(".py", ignoreCase = true)
    }
}

/**
 * Validation result data class
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String
)
