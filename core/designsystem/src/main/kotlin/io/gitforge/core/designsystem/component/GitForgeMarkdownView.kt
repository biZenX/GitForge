package io.gitforge.core.designsystem.component

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.theme.CharcoalSurfaceContainerLowestDark
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.GoogleSansCodeFamily
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Material 3 Expressive Markdown Viewer with touch-friendly code snippet copy buttons.
 * Renders headers, lists, blockquotes, inline formatting, and fenced code blocks.
 */
@Composable
fun GitForgeMarkdownView(
    markdownContent: String,
    modifier: Modifier = Modifier
) {
    val blocks = remember(markdownContent) { parseMarkdownBlocks(markdownContent) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Header -> {
                    MarkdownHeaderView(block)
                }
                is MarkdownBlock.CodeBlock -> {
                    MarkdownCodeBlockView(block)
                }
                is MarkdownBlock.ListItem -> {
                    MarkdownListItemView(block)
                }
                is MarkdownBlock.BlockQuote -> {
                    MarkdownBlockQuoteView(block)
                }
                is MarkdownBlock.Paragraph -> {
                    MarkdownParagraphView(block)
                }
                is MarkdownBlock.Divider -> {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkdownHeaderView(header: MarkdownBlock.Header) {
    val style = when (header.level) {
        1 -> MaterialTheme.typography.titleLarge.copy(
            fontFamily = GoogleSansFlexFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        2 -> MaterialTheme.typography.titleMedium.copy(
            fontFamily = GoogleSansFlexFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TerracottaPrimaryDark
        )
        else -> MaterialTheme.typography.titleSmall.copy(
            fontFamily = GoogleSansFlexFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = CyanAccentDark
        )
    }

    Text(
        text = header.text,
        style = style,
        modifier = Modifier.padding(top = if (header.level <= 2) 8.dp else 4.dp)
    )
}

@Composable
private fun MarkdownCodeBlockView(codeBlock: MarkdownBlock.CodeBlock) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SubCardShape,
        color = CharcoalSurfaceContainerLowestDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            // Header Bar: Language Badge + Touch-Friendly Copy Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF14171C))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Language Pill
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(TerracottaPrimaryDark.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = codeBlock.language.ifBlank { "CODE" }.uppercase(),
                        fontFamily = GoogleSansCodeFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TerracottaPrimaryDark
                    )
                }

                // Copy Button (Touch & Laptop accessible)
                Row(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(codeBlock.code))
                            isCopied = true
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                delay(2000)
                                isCopied = false
                            }
                        }
                        .background(if (isCopied) EmeraldAccentDark.copy(alpha = 0.18f) else Color(0x1AFFFFFF))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (isCopied) EmeraldAccentDark else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isCopied) "Copied!" else "Copy",
                        style = CodeTypography.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCopied) EmeraldAccentDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Code Content Box with Horizontal Scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = codeBlock.code,
                    style = CodeTypography.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFFE2E8F0)
                    )
                )
            }
        }
    }
}

@Composable
private fun MarkdownListItemView(item: MarkdownBlock.ListItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp, end = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(TerracottaPrimaryDark)
        )
        Text(
            text = renderInlineFormatting(item.text),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = GoogleSansFlexFamily,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 21.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MarkdownBlockQuoteView(quote: MarkdownBlock.BlockQuote) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(24.dp)
                    .background(TerracottaPrimaryDark, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = renderInlineFormatting(quote.text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = GoogleSansFlexFamily,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun MarkdownParagraphView(paragraph: MarkdownBlock.Paragraph) {
    Text(
        text = renderInlineFormatting(paragraph.text),
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = GoogleSansFlexFamily,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )
    )
}

// Sealed hierarchy for parsed Markdown structure
sealed interface MarkdownBlock {
    data class Header(val level: Int, val text: String) : MarkdownBlock
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock
    data class ListItem(val text: String) : MarkdownBlock
    data class BlockQuote(val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    object Divider : MarkdownBlock
}

private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val lines = raw.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var inCodeBlock = false
    var codeLang = ""
    val codeLines = mutableListOf<String>()

    for (line in lines) {
        val trimmed = line.trimEnd()

        if (trimmed.startsWith("```")) {
            if (inCodeBlock) {
                blocks.add(MarkdownBlock.CodeBlock(codeLang, codeLines.joinToString("\n")))
                codeLines.clear()
                codeLang = ""
                inCodeBlock = false
            } else {
                inCodeBlock = true
                codeLang = trimmed.removePrefix("```").trim()
            }
            continue
        }

        if (inCodeBlock) {
            codeLines.add(line)
            continue
        }

        val stripped = trimmed.trim()
        when {
            stripped.isEmpty() -> continue
            stripped.startsWith("---") || stripped.startsWith("***") || stripped.startsWith("___") -> {
                blocks.add(MarkdownBlock.Divider)
            }
            stripped.startsWith("### ") -> blocks.add(MarkdownBlock.Header(3, stripped.removePrefix("### ")))
            stripped.startsWith("## ") -> blocks.add(MarkdownBlock.Header(2, stripped.removePrefix("## ")))
            stripped.startsWith("# ") -> blocks.add(MarkdownBlock.Header(1, stripped.removePrefix("# ")))
            stripped.startsWith("- ") || stripped.startsWith("* ") || stripped.startsWith("• ") -> {
                val itemText = stripped.removePrefix("- ").removePrefix("* ").removePrefix("• ")
                blocks.add(MarkdownBlock.ListItem(itemText))
            }
            stripped.startsWith("> ") -> {
                blocks.add(MarkdownBlock.BlockQuote(stripped.removePrefix("> ")))
            }
            else -> {
                blocks.add(MarkdownBlock.Paragraph(stripped))
            }
        }
    }

    if (inCodeBlock && codeLines.isNotEmpty()) {
        blocks.add(MarkdownBlock.CodeBlock(codeLang, codeLines.joinToString("\n")))
    }

    return blocks
}

// Basic Inline formatting for bold (**text**), italics (*text*), and code (`text`)
private fun renderInlineFormatting(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val pattern = Regex("(\\*\\*.*?\\*\\*|`.*?`|\\*.*?\\*)")
        val matches = pattern.findAll(text)

        for (match in matches) {
            if (match.range.first > cursor) {
                append(text.substring(cursor, match.range.first))
            }

            val value = match.value
            when {
                value.startsWith("**") && value.endsWith("**") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TerracottaPrimaryDark))
                    append(value.substring(2, value.length - 2))
                    pop()
                }
                value.startsWith("`") && value.endsWith("`") -> {
                    pushStyle(SpanStyle(fontFamily = GoogleSansCodeFamily, color = CyanAccentDark, background = Color(0x2A38BDF8)))
                    append(value.substring(1, value.length - 1))
                    pop()
                }
                value.startsWith("*") && value.endsWith("*") -> {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(value.substring(1, value.length - 1))
                    pop()
                }
                else -> append(value)
            }
            cursor = match.range.last + 1
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}
