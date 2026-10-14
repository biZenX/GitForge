package io.gitforge.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.gitforge.app.ui.navigation.GitForgeNavigationRoot
import io.gitforge.app.ui.settings.SettingsViewModel
import io.gitforge.core.designsystem.theme.GitForgeTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleOAuthIntent(intent)

        setContent {
            GitForgeTheme {
                GitForgeNavigationRoot()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthIntent(intent)
    }

    private fun handleOAuthIntent(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.scheme == "gitforge" && uri.host == "oauth") {
            settingsViewModel.handleOAuthCallback(uri)
        }
    }
}
