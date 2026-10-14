package io.gitforge.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.theme.StatusDraft
import io.gitforge.core.designsystem.theme.StatusError
import io.gitforge.core.designsystem.theme.StatusMerged
import io.gitforge.core.designsystem.theme.StatusSuccess
import io.gitforge.core.designsystem.theme.StatusWarning

@Composable
fun GitForgeStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "open" -> StatusSuccess.copy(alpha = 0.15f) to StatusSuccess
        "closed" -> StatusError.copy(alpha = 0.15f) to StatusError
        "merged" -> StatusMerged.copy(alpha = 0.15f) to StatusMerged
        "draft" -> StatusDraft.copy(alpha = 0.15f) to StatusDraft
        "success" -> StatusSuccess.copy(alpha = 0.15f) to StatusSuccess
        "failure" -> StatusError.copy(alpha = 0.15f) to StatusError
        "queued", "in_progress" -> StatusWarning.copy(alpha = 0.15f) to StatusWarning
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            color = textColor
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
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
