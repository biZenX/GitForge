package io.gitforge.app.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.app.ui.home.HomeViewModel
import io.gitforge.app.ui.settings.SettingsViewModel
import io.gitforge.core.designsystem.component.EssentialsGroupedItem
import io.gitforge.core.designsystem.component.GroupedCardPosition
import io.gitforge.core.designsystem.component.ScallopedShape
import io.gitforge.core.designsystem.component.bounceClick
import io.gitforge.core.designsystem.theme.GoogleSansCodeFamily
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.PillBadgeShape

@Composable
fun ProfileScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val user = homeState.user
    val uriHandler = LocalUriHandler.current

    val devName = user?.name ?: user?.login ?: "biZenX"
    val devLogin = user?.login ?: "biZenX"
    val avatarUrl = user?.avatarUrl ?: "https://github.com/biZenX.png"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Essentials Signature Developer Header Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1E2128),
                border = BorderStroke(1.dp, Color(0x14FFFFFF))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GitForge Developer Studio",
                        fontFamily = GoogleSansFlexFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "The all-in-one developer toolbox for Android & GitHub",
                        fontFamily = GoogleSansFlexFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF9AA0A6)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 12-Lobed Scalloped Flower Avatar Frame (Essentials signature)
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .clip(ScallopedShape(lobes = 12, amplitudeRatio = 0.12f))
                            .background(Color(0xFF265EA7))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = devName,
                        fontFamily = GoogleSansFlexFamily,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/$devLogin")
                        }
                    ) {
                        Text(
                            text = "@$devLogin",
                            fontFamily = GoogleSansCodeFamily,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA8C7FA)
                        )
                        Text(
                            text = " • Local JGit Engine Active",
                            fontFamily = GoogleSansFlexFamily,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF81C784)
                        )
                    }

                    if (!user?.bio.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user?.bio ?: "",
                            fontFamily = GoogleSansFlexFamily,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFB0B7C3),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Pastel Squircle Action Buttons (Web, Code, Releases)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileSquircleAction(
                            icon = Icons.Default.Language,
                            tint = Color(0xFF001D35),
                            containerColor = Color(0xFFD0E4FF),
                            onClick = { uriHandler.openUri("https://bizenx.github.io/GitForge/") }
                        )
                        ProfileSquircleAction(
                            icon = Icons.Default.Code,
                            tint = Color(0xFF00391A),
                            containerColor = Color(0xFFC4EED0),
                            onClick = { uriHandler.openUri("https://github.com/biZenX/GitForge") }
                        )
                        ProfileSquircleAction(
                            icon = Icons.Default.NewReleases,
                            tint = Color(0xFF381E72),
                            containerColor = Color(0xFFEADDFF),
                            onClick = { uriHandler.openUri("https://github.com/biZenX/GitForge/releases") }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileMetricBadge(
                            label = "Repositories",
                            count = "${user?.publicRepos ?: homeState.recentRepositories.size}"
                        )
                        ProfileMetricBadge(
                            label = "Followers",
                            count = "${user?.followers ?: 0}"
                        )
                        ProfileMetricBadge(
                            label = "Tracking Apps",
                            count = "${homeState.watchedReleases.size}"
                        )
                    }

                    // Metadata Row
                    if (!user?.email.isNullOrBlank() || !user?.location.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!user?.email.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mail, contentDescription = null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA0A6))
                                }
                            }
                            if (!user?.location.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF9AA0A6), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(user?.location ?: "", style = MaterialTheme.typography.bodySmall, color = Color(0xFF9AA0A6))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: System & Engine Settings
        item {
            Text(
                text = "Studio Environment & Security",
                fontFamily = GoogleSansFlexFamily,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDF0F5)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                EssentialsGroupedItem(
                    title = if (settingsState.isAuthenticated) "GitHub Account Connected" else "Connect GitHub Account",
                    subtitle = if (settingsState.isAuthenticated) "Token: ${settingsState.tokenPreview}" else "Configure Personal Access Token or OAuth",
                    icon = Icons.Default.Security,
                    iconBackgroundColor = Color(0xFFC2E7FF),
                    iconTintColor = Color(0xFF004A77),
                    position = GroupedCardPosition.Top,
                    onClick = onNavigateToSettings
                )

                EssentialsGroupedItem(
                    title = "In-App Updates & Releases",
                    subtitle = "GitForge v1.0.6 • FileProvider Native Updater",
                    icon = Icons.Default.RocketLaunch,
                    iconBackgroundColor = Color(0xFFC4EED0),
                    iconTintColor = Color(0xFF00522B),
                    position = GroupedCardPosition.Middle,
                    onClick = onNavigateToSettings
                )

                EssentialsGroupedItem(
                    title = "Sandboxed JGit Engine",
                    subtitle = "100% on-device Git operations, staging & commit signing",
                    icon = Icons.Default.Terminal,
                    iconBackgroundColor = Color(0xFFEADDFF),
                    iconTintColor = Color(0xFF4F378B),
                    position = GroupedCardPosition.Middle,
                    onClick = {}
                )

                EssentialsGroupedItem(
                    title = "Encrypted Local Storage",
                    subtitle = "Room DB & Android Keystore-backed secrets",
                    icon = Icons.Default.Storage,
                    iconBackgroundColor = Color(0xFFFFDCC2),
                    iconTintColor = Color(0xFF3E1C00),
                    position = GroupedCardPosition.Bottom,
                    onClick = { settingsViewModel.clearCache() }
                )
            }
        }

        // Section: Account Actions
        item {
            if (settingsState.isAuthenticated) {
                OutlinedButton(
                    onClick = { settingsViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .bounceClick { settingsViewModel.logout() },
                    shape = PillBadgeShape,
                    border = BorderStroke(1.dp, Color(0x33E57373)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out & Remove Token", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .bounceClick(onClick = onNavigateToSettings),
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA8C7FA))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color(0xFF041E49), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign In with GitHub", fontWeight = FontWeight.Bold, color = Color(0xFF041E49))
                }
            }
        }
    }
}

@Composable
private fun ProfileSquircleAction(
    icon: ImageVector,
    tint: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileMetricBadge(label: String, count: String) {
    Surface(
        shape = PillBadgeShape,
        color = Color(0xFF262A33),
        border = BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = count,
                fontFamily = GoogleSansCodeFamily,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDF0F5)
            )
            Text(
                text = label,
                fontFamily = GoogleSansFlexFamily,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9AA0A6)
            )
        }
    }
}
