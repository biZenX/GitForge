package io.gitforge.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
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
import io.gitforge.core.designsystem.theme.PillBadgeShape

/**
 * Authentic Wafeer-Style Hero Banner ("يافطة وفير الكبرى").
 * Direct replica of the layout, colors, and curved organic accents in photo_2026-10-15_19-29-15.jpg.
 */
@Composable
fun WafeerHeroBanner(
    title: String,
    primaryMetricValue: String,
    primaryMetricLabel: String,
    primaryMetricAvailable: String,
    secondaryLeftTitle: String,
    secondaryLeftValue: String,
    secondaryLeftSubtext: String,
    badgeCount: String,
    badgeLabel: String,
    filterOptions: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color(0xFF141915),
        border = BorderStroke(1.dp, Color(0xFF233026))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Drag Handle Pill
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF374151))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section Header: Title + Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF3F4F6)
                )

                if (onEditClick != null) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Forest Green Card with Organic Curved Shape Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF2E531C))
                    .padding(18.dp)
            ) {
                // Background curved artistic shape
                Canvas(modifier = Modifier.matchParentSize()) {
                    val path = Path()
                    path.moveTo(0f, 0f)
                    path.lineTo(size.width * 0.28f, 0f)
                    path.cubicTo(
                        size.width * 0.22f, size.height * 0.4f,
                        size.width * 0.12f, size.height * 0.8f,
                        0f, size.height
                    )
                    path.close()
                    drawPath(path, color = Color(0x334ADE80))
                }

                Column(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = primaryMetricValue,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF3F4F6)
                    )
                    Text(
                        text = primaryMetricLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFD1D5DB)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = primaryMetricAvailable,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFA7F3D0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two-Column Row: Left Ivory/Obsidian Card + Right Scalloped Flower Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Card: Light Ivory/Cream Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(104.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFE8E5DA))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = secondaryLeftValue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1F2937)
                        )
                        Text(
                            text = secondaryLeftTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = secondaryLeftSubtext,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF374151)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Right: Scalloped Flower Badge
                WafeerFlowerBadge(
                    countText = badgeCount,
                    labelText = badgeLabel,
                    badgeColor = Color(0xFF16271A),
                    borderColor = Color(0xFF4ADE80),
                    arcColor = Color(0xFF86EFAC),
                    trackColor = Color(0x3386EFAC)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-question / Segmented Filter Title
            Text(
                text = "Filter Workspace Repositories",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9CA3AF),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pill Segmented Filters Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { option ->
                    val isSelected = option == selectedFilter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(PillBadgeShape)
                            .background(if (isSelected) Color(0xFF3B6623) else Color(0xFF1F2937))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF4ADE80) else Color(0x229CA3AF),
                                PillBadgeShape
                            )
                            .bounceClick { onFilterSelected(option) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}
