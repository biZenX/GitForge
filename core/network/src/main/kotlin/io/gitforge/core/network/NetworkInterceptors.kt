package io.gitforge.core.network

import io.gitforge.core.security.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { tokenStorage.getToken() }

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "GitForge-Android")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            // Unauthorized / Token expired or revoked
            // We can notify listeners or clear invalid token
        }

        return response
    }
}

class RateLimitInfo(
    val limit: Int,
    val remaining: Int,
    val resetEpochSeconds: Long
)

class RateLimitException(
    val remaining: Int,
    val resetEpochSeconds: Long,
    override val message: String = "GitHub API rate limit exceeded. Reset in ${(resetEpochSeconds - System.currentTimeMillis() / 1000).coerceAtLeast(0)} seconds."
) : IOException(message)

class RateLimitInterceptor(
    private val onRateLimitUpdated: ((RateLimitInfo) -> Unit)? = null
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val limit = response.header("x-ratelimit-limit")?.toIntOrNull()
        val remaining = response.header("x-ratelimit-remaining")?.toIntOrNull()
        val reset = response.header("x-ratelimit-reset")?.toLongOrNull()

        if (limit != null && remaining != null && reset != null) {
            val info = RateLimitInfo(limit, remaining, reset)
            onRateLimitUpdated?.invoke(info)

            if (remaining == 0 && response.code == 403) {
                throw RateLimitException(remaining, reset)
            }
        }

        return response
    }
}
