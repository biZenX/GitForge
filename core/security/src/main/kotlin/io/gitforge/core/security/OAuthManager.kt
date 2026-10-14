package io.gitforge.core.security

import android.net.Uri
import java.security.SecureRandom
import java.util.concurrent.atomic.AtomicReference

class OAuthManager(
    private val clientId: String = "Ov23liDEFAULT_GITFORGE", // Configurable at runtime or BuildConfig
    private val redirectUri: String = "gitforge://oauth/callback"
) {
    private val secureRandom = SecureRandom()
    private val pendingState = AtomicReference<String?>(null)

    fun generateAuthorizationUrl(customClientId: String? = null): Pair<String, String> {
        val bytes = ByteArray(24)
        secureRandom.nextBytes(bytes)
        val state = bytes.joinToString("") { "%02x".format(it) }
        pendingState.set(state)

        val activeClientId = customClientId?.takeIf { it.isNotBlank() } ?: clientId
        val encodedRedirect = java.net.URLEncoder.encode(redirectUri, "UTF-8")
        val encodedScope = java.net.URLEncoder.encode("repo,workflow,user,read:org,notifications,gist", "UTF-8")
        val url = "https://github.com/login/oauth/authorize?client_id=$activeClientId&redirect_uri=$encodedRedirect&scope=$encodedScope&state=$state"

        return Pair(url, state)
    }

    fun verifyCallback(uri: Uri): Result<String> {
        val error = uri.getQueryParameter("error")
        if (error != null) {
            val description = uri.getQueryParameter("error_description") ?: "Authentication was denied."
            return Result.failure(SecurityException("OAuth Error: $error ($description)"))
        }

        val returnedState = uri.getQueryParameter("state")
        val expectedState = pendingState.getAndSet(null)

        if (expectedState == null || returnedState != expectedState) {
            return Result.failure(SecurityException("OAuth State Mismatch: Possible CSRF attack detected."))
        }

        val code = uri.getQueryParameter("code")
        return if (!code.isNullOrBlank()) {
            Result.success(code)
        } else {
            Result.failure(IllegalArgumentException("Callback missing authorization code."))
        }
    }

    fun validatePersonalAccessToken(token: String): Boolean {
        val trimmed = token.trim()
        // Classic PAT: 40 chars, Fine-grained: github_pat_..., OAuth: gho_...
        return trimmed.length >= 30 && (
            trimmed.startsWith("ghp_") ||
            trimmed.startsWith("github_pat_") ||
            trimmed.startsWith("gho_") ||
            trimmed.matches(Regex("^[a-fA-F0-9]{40}$"))
        )
    }
}
