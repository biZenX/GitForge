package io.gitforge.core.security

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OAuthManagerTest {

    private lateinit var oauthManager: OAuthManager

    @Before
    fun setup() {
        oauthManager = OAuthManager(
            clientId = "test_client_id",
            redirectUri = "gitforge://oauth/callback"
        )
    }

    @Test
    fun `generateAuthorizationUrl contains required scopes and valid state`() {
        val (url, state) = oauthManager.generateAuthorizationUrl()

        assertThat(url).contains("https://github.com/login/oauth/authorize")
        assertThat(url).contains("client_id=test_client_id")
        assertThat(url).contains("scope=repo%2Cworkflow%2Cuser%2Cread%3Aorg%2Cnotifications%2Cgist")
        assertThat(url).contains("state=$state")
        assertThat(state).isNotEmpty()
    }

    @Test
    fun `validatePersonalAccessToken correctly validates tokens`() {
        // Classic PAT
        assertThat(oauthManager.validatePersonalAccessToken("ghp_1234567890abcdef1234567890abcdef1234")).isTrue()

        // Fine-grained PAT
        assertThat(oauthManager.validatePersonalAccessToken("github_pat_11ABCD_123456789012345678901234567890")).isTrue()

        // OAuth token
        assertThat(oauthManager.validatePersonalAccessToken("gho_1234567890abcdef1234567890abcdef1234")).isTrue()

        // Invalid tokens
        assertThat(oauthManager.validatePersonalAccessToken("")).isFalse()
        assertThat(oauthManager.validatePersonalAccessToken("short_invalid")).isFalse()
        assertThat(oauthManager.validatePersonalAccessToken("not_a_github_token_at_all_1234567890")).isFalse()
    }
}
