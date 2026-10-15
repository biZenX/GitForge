package io.gitforge.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.PillBadgeShape

/**
 * Authentic Developer Workspace Hero Banner.
 * Pure Material 3 Expressive developer workspace with real dynamic metrics,
 * live repository search, filter chips, and developer profile identity.
 */
@Composable
fun GitForgeDeveloperWorkspaceHero(
    developerName: String,
    developerHandle: String,
    avatarUrl: String?,
    repositoryCount: Int,
    starredCount: Int,
    trackedReleasesCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterOptions: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF1E2128),
        border = BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Drag Handle Pill
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3C404B))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Developer Identity & Sync Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with rounded squircle frame
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Developer Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, Color(0x33A8C7FA), RoundedCornerShape(16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF262A33))
                            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color(0xFFA8C7FA),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = developerName,
                        fontFamily = GoogleSansFlexFamily,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
                    )
                    Text(
                        text = "$developerHandle • Local JGit Engine Active",
                        fontFamily = GoogleSansFlexFamily,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA8C7FA),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Refresh / Sync Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0x14FFFFFF))
                        .bounceClick(onClick = onRefresh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync",
                        tint = Color(0xFFEDF0F5),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Repositories Stat
                WorkspaceMetricPill(
                    count = "$repositoryCount",
                    label = "Repositories",
                    icon = Icons.Outlined.Folder,
                    tint = Color(0xFFA8C7FA),
                    modifier = Modifier.weight(1f)
                )

                // Starred Stat
                WorkspaceMetricPill(
                    count = "$starredCount",
                    label = "Starred",
                    icon = Icons.Default.Star,
                    tint = AmberAccentDark,
                    modifier = Modifier.weight(1f)
                )

                // Tracking Stat
                WorkspaceMetricPill(
                    count = "$trackedReleasesCount",
                    label = "Tracking",
                    icon = Icons.Default.NotificationsActive,
                    tint = Color(0xFF81C784),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-Time Search Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PillBadgeShape,
                color = Color(0xFF16181D),
                border = BorderStroke(1.dp, Color(0x1FFFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9AA0A6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFEDF0F5),
                            fontFamily = GoogleSansFlexFamily
                        ),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search in $repositoryCount repositories...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = GoogleSansFlexFamily,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF9AA0A6),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { filter ->
                    val isSelected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(if (isSelected) Color(0xFFA8C7FA) else Color(0xFF262A33))
                            .border(1.dp, if (isSelected) Color(0xFFA8C7FA) else Color(0x14FFFFFF), PillBadgeShape)
                            .bounceClick { onFilterSelected(filter) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            fontFamily = GoogleSansFlexFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF041E49) else Color(0xFFEDF0F5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkspaceMetricPill(
    count: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF16181D),
        border = BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = count,
                    fontFamily = GoogleSansFlexFamily,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEDF0F5)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = GoogleSansFlexFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9AA0A6)
            )
        }
    }
}
