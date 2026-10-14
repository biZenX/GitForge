package io.gitforge.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.theme.HeroCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark

/**
 * Authentic Material 3 Expressive Creator Credit Card for Arkim (@biZenX).
 * Displays creator avatar, GitHub link, role pills, and GNU GPLv3 copyright notice.
 */
@Composable
fun GitForgeCreatorCard(
    modifier: Modifier = Modifier,
    creatorName: String = "Arkim",
    creatorHandle: String = "biZenX",
    creatorAvatarUrl: String = "https://github.com/biZenX.png",
    profileUrl: String = "https://github.com/biZenX"
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = HeroCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row: Avatar + Name + Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 28dp / 18dp Squircle Avatar with M3 Terracotta border
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, TerracottaPrimaryDark, RoundedCornerShape(20.dp))
                ) {
                    AsyncImage(
                        model = creatorAvatarUrl,
                        contentDescription = "$creatorName's GitHub Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = creatorName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Creator",
                            tint = TerracottaPrimaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "@$creatorHandle",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TerracottaPrimaryDark,
                        modifier = Modifier.clickable {
                            uriHandler.openUri(profileUrl)
                        }
                    )
                }

                // M3 Pill Badge
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(TerracottaPrimaryDark.copy(alpha = 0.15f))
                        .border(1.dp, TerracottaPrimaryDark.copy(alpha = 0.35f), PillBadgeShape)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Creator",
                        style = MaterialTheme.typography.labelSmall,
                        color = TerracottaPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bio description
            Text(
                text = "Lead architect and developer of GitForge. Engineered with native Jetpack Compose, Material 3 Expressive design, on-device JGit engine, and sandboxed developer workspace.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.25f
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer row with Copyright & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "© 2026 $creatorName (@$creatorHandle)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "GNU General Public License v3.0 (GPLv3)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedButton(
                    onClick = { uriHandler.openUri(profileUrl) },
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TerracottaPrimaryDark
                    ),
                    border = BorderStroke(1.dp, TerracottaPrimaryDark.copy(alpha = 0.4f))
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GitHub",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
