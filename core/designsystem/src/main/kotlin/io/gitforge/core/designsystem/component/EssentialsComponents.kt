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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

enum class GroupedCardPosition {
    Top, Middle, Bottom, Single
}

/**
 * Authentic Essentials Grouped List Item with pastel circle icon.
 * Replicated directly from photo_2026-10-15_19-29-29.jpg.
 */
@Composable
fun EssentialsGroupedItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    position: GroupedCardPosition = GroupedCardPosition.Middle,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val shape = when (position) {
        GroupedCardPosition.Top -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        GroupedCardPosition.Middle -> RoundedCornerShape(4.dp)
        GroupedCardPosition.Bottom -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
        GroupedCardPosition.Single -> RoundedCornerShape(24.dp)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
        shape = shape,
        color = Color(0xFF1E232B),
        border = BorderStroke(1.dp, Color(0x1A8B949E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Soft Pastel Circle Icon Container (Essentials style)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF3F4F6)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Respiro-Style Full Width Mint Pill Button.
 * Replicated from photo_2026-10-15_19-29-24.jpg.
 */
@Composable
fun RespiroPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = Color(0xFFB7F397),
    textColor: Color = Color(0xFF142010)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .bounceClick(onClick = onClick),
        shape = PillBadgeShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
    }
}

/**
 * Essentials-Style Floating Navigation Dock.
 * Replicated from photo_2026-10-15_19-29-29.jpg and photo_2026-10-15_19-28-47.jpg.
 */
@Composable
fun EssentialsFloatingDock(
    items: List<Pair<String, ImageVector>>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Floating Wide Pill Dock
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            shape = PillBadgeShape,
            color = Color(0xFF1E2128),
            border = BorderStroke(1.dp, Color(0x22FFFFFF)),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEachIndexed { index, pair ->
                    val isSelected = index == selectedIndex
                    if (isSelected) {
                        // Selected Capsule Pill
                        Box(
                            modifier = Modifier
                                .height(44.dp)
                                .clip(PillBadgeShape)
                                .background(Color(0xFF2B477A))
                                .border(1.dp, Color(0x33A8C7FA), PillBadgeShape)
                                .padding(horizontal = 16.dp)
                                .bounceClick { onItemSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = pair.second,
                                    contentDescription = pair.first,
                                    tint = Color(0xFFD3E3FD),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pair.first,
                                    color = Color(0xFFD3E3FD),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Unselected Icon
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .bounceClick { onItemSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pair.second,
                                contentDescription = pair.first,
                                tint = Color(0xFF9AA0A6),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Separate Floating Squircle Action Button (Essentials signature)
        Surface(
            modifier = Modifier
                .size(64.dp)
                .bounceClick(onClick = onActionClick),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF234275),
            border = BorderStroke(1.dp, Color(0x33A8C7FA)),
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Action",
                    tint = Color(0xFFD3E3FD),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
