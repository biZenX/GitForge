package io.gitforge.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.theme.PillBadgeShape

data class ReleaseItem(
    val title: String,
    val description: String
)

data class VersionChangelog(
    val version: String,
    val releaseDate: String,
    val newFeatures: List<ReleaseItem>,
    val improvements: List<ReleaseItem>,
    val bugFixes: List<ReleaseItem>
)

/**
 * Authentic Wafeer "What's New" Sheet ("يافطة تحديثات وفير").
 * Direct replica of the layout, typography, vertical accent lines, and wavy dividers in video_2026-10-15_19-29-20.mp4.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitForgeWhatsNewSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val changelogs = listOf(
        VersionChangelog(
            version = "GitForge v1.0.5",
            releaseDate = "2026-10-15",
            newFeatures = listOf(
                ReleaseItem("Pure Material 3 Pastels", "Removed all harsh saturated/neon colors. Refined with soothing Google Pixel Periwinkle, Sage Mint, Soft Lavender, and warm Slate surfaces."),
                ReleaseItem("Essentials Floating Dock", "Floating bottom navigation capsule dock with animated pill indicator and squircle terminal launcher."),
                ReleaseItem("Interactive Live Search & Filter", "Fully responsive in-memory search and tag filtering (All, Starred, Sources, Forks) with real-time UI updates."),
                ReleaseItem("Refined Wafeer Header", "Elevated slate surface container with live repository metrics and branch synchronization status.")
            ),
            improvements = listOf(
                ReleaseItem("80dp Content Clearance", "Added dynamic content bottom clearance on all primary screens to ensure perfect readability over the floating dock."),
                ReleaseItem("Zero Neon Policy", "Every single container, badge, chip, and border unified under authentic matte Material 3 palette.")
            ),
            bugFixes = listOf(
                ReleaseItem("Filter Recomposition", "Resolved filter selection state persistence across repository lists.")
            )
        ),
        VersionChangelog(
            version = "GitForge v1.0.4",
            releaseDate = "2026-10-15",
            newFeatures = listOf(
                ReleaseItem("Wafeer Hero Banner ('يافطة وفير')", "Deep forest green curved container with big bold metrics, branch status, and pill segmented filter."),
                ReleaseItem("12-Lobed Scallop Flower Badges", "Custom parametric scallop/flower badges with animated circular progress arcs."),
                ReleaseItem("Essentials Grouped Cards & Pastel Icons", "Connected rounded list cards with soft lavender, cyan, mint, and apricot circular badges."),
                ReleaseItem("Essentials About & Profile", "Scalloped flower avatar, social squircles, repo statistics pill, and community contributors.")
            ),
            improvements = listOf(
                ReleaseItem("Zero Watermarks", "Clean, respectful developer experience with @biZenX GitHub profile integration."),
                ReleaseItem("Wavy Dividers", "Authentic sinusoidal sine-wave dividers between versions and changelog sections.")
            ),
            bugFixes = listOf(
                ReleaseItem("Guest Profile State", "Clean Developer Workspace with offline-first support and sign-in CTA.")
            )
        ),
        VersionChangelog(
            version = "GitForge v1.0.3",
            releaseDate = "2026-10-15",
            newFeatures = listOf(
                ReleaseItem("Supercharged Markdown Engine", "Rendered document view, formatted tables, checklists, callouts, and one-tap Copy All."),
                ReleaseItem("Dynamic Branch Switcher", "Modal sheet with real-time search, instant tree reloading, and active indicators.")
            ),
            improvements = listOf(
                ReleaseItem("In-App File Viewer", "Syntax-highlighted code with line numbers and 1-tap open in GitForge Studio."),
                ReleaseItem("Obsidian M3 Theme", "High-contrast dark obsidian palette with pure blacks and rich accents.")
            ),
            bugFixes = listOf(
                ReleaseItem("Dynamic Monet Override Fixed", "Prevented Android 12+ wallpaper tints from washing out app colors.")
            )
        ),
        VersionChangelog(
            version = "GitForge v1.0.2",
            releaseDate = "2026-10-15",
            newFeatures = listOf(
                ReleaseItem("In-App Updater & Watch Releases", "Instant GitHub API updates and notifications for followed repositories.")
            ),
            improvements = listOf(
                ReleaseItem("Spring Physics BounceClick", "Subtle haptic-friendly micro-animations on interactive cards and buttons.")
            ),
            bugFixes = listOf(
                ReleaseItem("Repository Details Stability", "Fixed asset download links and token authentication headers.")
            )
        ),
        VersionChangelog(
            version = "GitForge v1.0.0",
            releaseDate = "2026-10-14",
            newFeatures = listOf(
                ReleaseItem("Production Release", "Pure on-device JGit engine, syntax-highlighted Code Studio, and sandboxed Terminal.")
            ),
            improvements = listOf(
                ReleaseItem("Multi-Module Architecture", "Decoupled Clean Architecture across core and feature modules.")
            ),
            bugFixes = listOf(
                ReleaseItem("Initial Release", "Full test suite pass and zero tracking verification.")
            )
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF16181D),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Row: Back Arrow + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What's New",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(changelogs) { changelog ->
                    // Version Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = changelog.version,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFA3E635) // Wafeer bright lime
                        )

                        Box(
                            modifier = Modifier
                                .clip(PillBadgeShape)
                                .background(Color(0xFF272F24))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = changelog.releaseDate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD1D5DB)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // New Features Section
                    if (changelog.newFeatures.isNotEmpty()) {
                        ChangelogSectionHeader(
                            title = "New Features",
                            icon = Icons.Default.RocketLaunch,
                            tint = Color(0xFFA3E635)
                        )
                        changelog.newFeatures.forEach { item ->
                            ChangelogItemRow(
                                title = item.title,
                                description = item.description,
                                accentColor = Color(0xFFA3E635)
                            )
                        }
                    }

                    // Changes & Improvements Section
                    if (changelog.improvements.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ChangelogSectionHeader(
                            title = "Changes & Improvements",
                            icon = Icons.Default.AutoAwesome,
                            tint = Color(0xFF86EFAC)
                        )
                        changelog.improvements.forEach { item ->
                            ChangelogItemRow(
                                title = item.title,
                                description = item.description,
                                accentColor = Color(0xFF86EFAC)
                            )
                        }
                    }

                    // Bug Fixes Section
                    if (changelog.bugFixes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ChangelogSectionHeader(
                            title = "Bug Fixes",
                            icon = Icons.Default.BugReport,
                            tint = Color(0xFFF87171)
                        )
                        changelog.bugFixes.forEach { item ->
                            ChangelogItemRow(
                                title = item.title,
                                description = item.description,
                                accentColor = Color(0xFFF87171)
                            )
                        }
                    }

                    // Wavy Divider between versions (Wafeer style)
                    WavyDivider(
                        text = "Previous version",
                        waveColor = Color(0xFF272F24),
                        textColor = Color(0xFF6B7280)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun ChangelogSectionHeader(
    title: String,
    icon: ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = tint
        )
    }
}

@Composable
private fun ChangelogItemRow(
    title: String,
    description: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        // Vertical Accent Bar (Wafeer signature)
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE5E7EB)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                lineHeight = 16.sp
            )
        }
    }
}
