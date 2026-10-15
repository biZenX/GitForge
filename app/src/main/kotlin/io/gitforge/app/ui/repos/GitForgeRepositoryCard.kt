package io.gitforge.app.ui.repos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.component.GitForgeLanguageDot
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.component.bounceClick
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.model.Repository

/**
 * Authentic Material 3 Repository Card.
 * Clean, modern 22dp container with soft pastel icons and optional watch release bell toggle.
 */
@Composable
fun GitForgeRepositoryCard(
    repository: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isWatched: Boolean = false,
    onToggleWatch: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF1E2128),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0x22A8C7FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (repository.private) Icons.Default.Lock else Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = Color(0xFFA8C7FA),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = repository.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
                    )
                    Text(
                        text = repository.fullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9AA0A6)
                    )
                }

                if (onToggleWatch != null) {
                    // Watch Release Bell Toggle
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isWatched) Color(0x22A8C7FA) else Color(0x0EFFFFFF))
                            .bounceClick(onClick = onToggleWatch),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWatched) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = "Watch releases",
                            tint = if (isWatched) Color(0xFFA8C7FA) else Color(0xFF9AA0A6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                GitForgeStatusBadge(
                    status = if (repository.private) "Private" else "Public",
                    showDot = false
                )
            }

            if (!repository.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = repository.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFB0B7C3),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                GitForgeLanguageDot(language = repository.language)
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(Color(0xFF262A33))
                        .border(1.dp, Color(0x14FFFFFF), PillBadgeShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = AmberAccentDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${repository.stargazersCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEDF0F5),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(Color(0xFF262A33))
                        .border(1.dp, Color(0x14FFFFFF), PillBadgeShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ForkRight,
                            contentDescription = "Forks",
                            tint = CyanAccentDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${repository.forksCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEDF0F5),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
