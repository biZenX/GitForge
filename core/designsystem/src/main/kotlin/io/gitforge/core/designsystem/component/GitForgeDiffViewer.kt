package io.gitforge.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.DiffAddBg
import io.gitforge.core.designsystem.theme.DiffAddText
import io.gitforge.core.designsystem.theme.DiffDeleteBg
import io.gitforge.core.designsystem.theme.DiffDeleteText
import io.gitforge.core.designsystem.theme.DiffHeaderBg

@Composable
fun GitForgeDiffViewer(
    patch: String?,
    modifier: Modifier = Modifier
) {
    if (patch.isNullOrBlank()) {
        Text(
            text = "No diff available for this file.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(16.dp)
        )
        return
    }

    val lines = patch.lines()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        var oldLineNum = 0
        var newLineNum = 0

        lines.forEachIndexed { index, line ->
            val (bgColor, textColor) = when {
                line.startsWith("@@") -> {
                    // Reset or parse chunk headers like @@ -1,4 +1,5 @@
                    val match = Regex("""@@ -(\d+)(?:,\d+)? \+(\d+)(?:,\d+)? @@""").find(line)
                    if (match != null) {
                        oldLineNum = match.groupValues[1].toInt() - 1
                        newLineNum = match.groupValues[2].toInt() - 1
                    }
                    DiffHeaderBg to MaterialTheme.colorScheme.primary
                }
                line.startsWith("+") && !line.startsWith("+++") -> {
                    newLineNum++
                    DiffAddBg to DiffAddText
                }
                line.startsWith("-") && !line.startsWith("---") -> {
                    oldLineNum++
                    DiffDeleteBg to DiffDeleteText
                }
                else -> {
                    if (line.isNotEmpty()) {
                        oldLineNum++
                        newLineNum++
                    }
                    Color.Transparent to MaterialTheme.colorScheme.onSurface
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(vertical = 2.dp)
            ) {
                // Line number column
                Text(
                    text = when {
                        line.startsWith("+") && !line.startsWith("+++") -> "+$newLineNum"
                        line.startsWith("-") && !line.startsWith("---") -> "-$oldLineNum"
                        line.startsWith("@@") -> "..."
                        else -> "$newLineNum"
                    },
                    style = CodeTypography,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .width(44.dp)
                        .padding(end = 8.dp)
                )

                // Code content
                Text(
                    text = line,
                    style = CodeTypography,
                    color = textColor,
                    modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                )
            }
        }
    }
}
