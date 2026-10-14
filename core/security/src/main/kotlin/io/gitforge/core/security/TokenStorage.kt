package io.gitforge.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    fun hasTokenFlow(): Flow<Boolean>
}

class EncryptedTokenStorage(private val context: Context) : TokenStorage {

    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "gitforge_secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback for tests or environments without Keystore provider
            context.getSharedPreferences("gitforge_fallback_prefs", Context.MODE_PRIVATE)
        }
    }

    override suspend fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token.trim()).apply()
    }

    override suspend fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)?.takeIf { it.isNotBlank() }
    }

    override suspend fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

    override fun hasTokenFlow(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_AUTH_TOKEN) {
                trySend(!prefs.getString(KEY_AUTH_TOKEN, null).isNullOrBlank())
            }
        }
        trySend(!prefs.getString(KEY_AUTH_TOKEN, null).isNullOrBlank())
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    companion object {
        private const val KEY_AUTH_TOKEN = "github_access_token"
    }
}
