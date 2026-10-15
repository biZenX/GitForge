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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.theme.PillBadgeShape

data class ContributorItem(
    val name: String,
    val avatarUrl: String,
    val score: Int
)

/**
 * Authentic Essentials About Card / Sheet.
 * Direct replica of the layout, scalloped flower avatar, social squircle buttons,
 * repo statistics pill, and contributor cards in photo_2026-10-15_19-28-47.jpg.
 */
@Composable
fun GitForgeAboutCard(
    modifier: Modifier = Modifier,
    version: String = "GitForge v1.0.3",
    onWhatsNewClick: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current

    val contributors = listOf(
        ContributorItem("biZenX", "https://github.com/biZenX.png", 67),
        ContributorItem("AndroidX", "https://avatars.githubusercontent.com/u/32689599?s=200&v=4", 49),
        ContributorItem("Kotlin", "https://avatars.githubusercontent.com/u/1446536?s=200&v=4", 39),
        ContributorItem("JGit", "https://avatars.githubusercontent.com/u/150085?s=200&v=4", 28)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1E2128),
        border = BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Title
            Text(
                text = version,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDF0F5)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "The all-in-one Git client & developer studio for Android",
                fontSize = 13.sp,
                color = Color(0xFF9AA0A6),
                fontWeight = FontWeight.Normal
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
                    model = "https://github.com/biZenX.png",
                    contentDescription = "biZenX Avatar",
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Developer Byline
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Crafted by ",
                    fontSize = 14.sp,
                    color = Color(0xFF9AA0A6)
                )
                Text(
                    text = "@biZenX",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA8C7FA),
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/biZenX")
                    }
                )
                Text(
                    text = " with ❤️",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Circular / Squircle Action Buttons (Web, Code, Releases)
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialSquircleButton(
                    icon = Icons.Default.Language,
                    tint = Color(0xFF001D35),
                    containerColor = Color(0xFFD0E4FF),
                    onClick = { uriHandler.openUri("https://bizenx.github.io/GitForge/") }
                )
                SocialSquircleButton(
                    icon = Icons.Default.Code,
                    tint = Color(0xFF00391A),
                    containerColor = Color(0xFFC4EED0),
                    onClick = { uriHandler.openUri("https://github.com/biZenX/GitForge") }
                )
                SocialSquircleButton(
                    icon = Icons.Default.NewReleases,
                    tint = Color(0xFF381E72),
                    containerColor = Color(0xFFEADDFF),
                    onClick = { uriHandler.openUri("https://github.com/biZenX/GitForge/releases") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Two Outlined Pill Buttons: [♥ Star on GitHub] [⊞ What's New ⌄]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { uriHandler.openUri("https://github.com/biZenX/GitForge") },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .bounceClick { uriHandler.openUri("https://github.com/biZenX/GitForge") },
                    shape = PillBadgeShape,
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF232730),
                        contentColor = Color(0xFFEDF0F5)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD56B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Star Repo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onWhatsNewClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .bounceClick(onClick = onWhatsNewClick),
                    shape = PillBadgeShape,
                    border = BorderStroke(1.dp, Color(0x1FFFFFFF)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF232730),
                        contentColor = Color(0xFFEDF0F5)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = null,
                        tint = Color(0xFFA8C7FA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("What's New ⌄", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GitHub Repo Pill: [ 🐙 biZenX/GitForge | ★ 3.3k | ⬇ 584k ]
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .bounceClick { uriHandler.openUri("https://github.com/biZenX/GitForge") },
                shape = PillBadgeShape,
                color = Color(0xFF171920),
                border = BorderStroke(1.dp, Color(0x14FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = Color(0xFFEDF0F5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "biZenX/GitForge",
                            color = Color(0xFFEDF0F5),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(PillBadgeShape)
                                .background(Color(0xFF423305))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "★ 3.3k",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD56B)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(PillBadgeShape)
                                .background(Color(0xFF163354))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "⬇ 584k",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA8C7FA)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Top Contributors Section (Essentials style)
            Text(
                text = "Core Contributors & Ecosystem",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEDF0F5),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(contributors) { contributor ->
                    ContributorAvatarCard(contributor)
                }
            }
        }
    }
}

@Composable
private fun SocialSquircleButton(
    icon: ImageVector,
    tint: Color,
    containerColor: Color = Color(0xFFD0E4FF),
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
private fun ContributorAvatarCard(contributor: ContributorItem) {
    Box(
        modifier = Modifier
            .size(width = 68.dp, height = 84.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1E2128))
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(18.dp))
    ) {
        AsyncImage(
            model = contributor.avatarUrl,
            contentDescription = contributor.name,
            modifier = Modifier
                .size(width = 68.dp, height = 84.dp)
                .clip(RoundedCornerShape(18.dp))
        )

        // Top Score Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .clip(PillBadgeShape)
                .background(Color(0xCC14161B))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "⚡ ${contributor.score}",
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFA8C7FA)
            )
        }
    }
}
