package io.gitforge.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.StatusDraft
import io.gitforge.core.designsystem.theme.StatusError
import io.gitforge.core.designsystem.theme.StatusMerged
import io.gitforge.core.designsystem.theme.StatusSuccess
import io.gitforge.core.designsystem.theme.StatusWarning

@Composable
fun GitForgeStatusBadge(
    status: String,
    modifier: Modifier = Modifier,
    showDot: Boolean = true
) {
    val (dotColor, bgColor, textColor) = when (status.lowercase()) {
        "open" -> Triple(StatusSuccess, StatusSuccess.copy(alpha = 0.12f), StatusSuccess)
        "closed" -> Triple(StatusError, StatusError.copy(alpha = 0.12f), StatusError)
        "merged" -> Triple(StatusMerged, StatusMerged.copy(alpha = 0.12f), StatusMerged)
        "draft" -> Triple(StatusDraft, StatusDraft.copy(alpha = 0.12f), StatusDraft)
        "success" -> Triple(StatusSuccess, StatusSuccess.copy(alpha = 0.12f), StatusSuccess)
        "failure" -> Triple(StatusError, StatusError.copy(alpha = 0.12f), StatusError)
        "queued", "in_progress" -> Triple(StatusWarning, StatusWarning.copy(alpha = 0.12f), StatusWarning)
        "public" -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), MaterialTheme.colorScheme.primary)
        "private" -> Triple(StatusWarning, StatusWarning.copy(alpha = 0.12f), StatusWarning)
        else -> Triple(
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Row(
        modifier = modifier
            .clip(PillBadgeShape)
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.25f), PillBadgeShape)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun GitForgeLanguageDot(
    language: String?,
    modifier: Modifier = Modifier
) {
    if (language.isNullOrBlank()) return

    val color = when (language.lowercase()) {
        "kotlin" -> Color(0xFFA97BFF)
        "java" -> Color(0xFFB07219)
        "python" -> Color(0xFF3572A5)
        "javascript" -> Color(0xFFF1E05A)
        "typescript" -> Color(0xFF3178C6)
        "html" -> Color(0xFFE34C26)
        "css" -> Color(0xFF563D7C)
        "c++" -> Color(0xFFF34B7D)
        "c" -> Color(0xFF555555)
        "rust" -> Color(0xFFDEA584)
        "go" -> Color(0xFF00ADD8)
        "shell" -> Color(0xFF89E051)
        "swift" -> Color(0xFFF05138)
        else -> Color(0xFF8B949E)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(PillBadgeShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, PillBadgeShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}
