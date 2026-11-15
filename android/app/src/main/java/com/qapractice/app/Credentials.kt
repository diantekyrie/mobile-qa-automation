package com.qapractice.app

/** Hard-coded practice login. This app has no backend; these are not real credentials. */
object Credentials {
    const val VALID_USERNAME = "tester"
    const val VALID_PASSWORD = "password123"

    /** Returns null when the login is valid, otherwise the string resource of the error to show. */
    fun check(username: String, password: String): Int? = when {
        username.isBlank() || password.isEmpty() -> R.string.error_required
        username != VALID_USERNAME || password != VALID_PASSWORD -> R.string.error_invalid
        else -> null
    }
}
