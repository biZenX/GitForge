package io.gitforge.core.designsystem.component

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.GoogleSansCodeFamily
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.RoseAccentDark
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Supercharged Material 3 Expressive Markdown & README Viewer.
 * Features full rich-text rendering, badges, tables, admonitions, checklists,
 * syntax-colored code snippets, clickable links, and a Rendered vs Raw view toggle.
 */
@Composable
fun GitForgeMarkdownView(
    markdownContent: String,
    modifier: Modifier = Modifier
) {
    var showRawMode by remember { mutableStateOf(false) }
    val blocks = remember(markdownContent) { parseMarkdownBlocks(markdownContent) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode Switcher Header: Rendered Document vs Raw Markdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(PillBadgeShape)
                .background(Color(0xFF161B22))
                .border(1.dp, Color(0x228B949E), PillBadgeShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rendered Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(PillBadgeShape)
                    .background(if (!showRawMode) TerracottaPrimaryDark else Color.Transparent)
                    .clickable { showRawMode = false }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (!showRawMode) Color(0xFF2E1003) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rendered View",
                        fontFamily = GoogleSansFlexFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (!showRawMode) Color(0xFF2E1003) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Raw Source Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(PillBadgeShape)
                    .background(if (showRawMode) TerracottaPrimaryDark else Color.Transparent)
                    .clickable { showRawMode = true }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = if (showRawMode) Color(0xFF2E1003) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Raw Markdown",
                        fontFamily = GoogleSansFlexFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (showRawMode) Color(0xFF2E1003) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        AnimatedContent(
            targetState = showRawMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "markdownViewMode"
        ) { isRaw ->
            if (isRaw) {
                RawMarkdownViewer(raw = markdownContent)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    blocks.forEach { block ->
                        when (block) {
                            is MarkdownBlock.Header -> MarkdownHeaderView(block)
                            is MarkdownBlock.CodeBlock -> MarkdownCodeBlockView(block)
                            is MarkdownBlock.BadgeRow -> MarkdownBadgeRowView(block)
                            is MarkdownBlock.Admonition -> MarkdownAdmonitionView(block)
                            is MarkdownBlock.Table -> MarkdownTableView(block)
                            is MarkdownBlock.TaskItem -> MarkdownTaskItemView(block)
                            is MarkdownBlock.ListItem -> MarkdownListItemView(block)
                            is MarkdownBlock.BlockQuote -> MarkdownBlockQuoteView(block)
                            is MarkdownBlock.Paragraph -> MarkdownParagraphView(block)
                            is MarkdownBlock.Divider -> {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0x228B949E))
                                        .padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownHeaderView(header: MarkdownBlock.Header) {
    Column(modifier = Modifier.padding(top = if (header.level <= 2) 10.dp else 4.dp)) {
        val (fontSize, color, isH1) = when (header.level) {
            1 -> Triple(22.sp, MaterialTheme.colorScheme.onSurface, true)
            2 -> Triple(18.sp, TerracottaPrimaryDark, true)
            3 -> Triple(16.sp, CyanAccentDark, false)
            else -> Triple(14.sp, EmeraldAccentDark, false)
        }

        Text(
            text = header.text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = GoogleSansFlexFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = fontSize,
                color = color
            )
        )

        if (isH1) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color(0x338B949E))
            )
        }
    }
}

@Composable
private fun MarkdownParagraphView(paragraph: MarkdownBlock.Paragraph) {
    val uriHandler = LocalUriHandler.current
    val annotated = renderInlineFormatting(paragraph.text)

    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = GoogleSansFlexFamily,
            color = Color(0xFFE6EDF3),
            lineHeight = 22.sp
        ),
        modifier = Modifier.clickable {
            // If text contains a URL, open first URL found
            val urlRegex = Regex("""https?://[^\s)\]]+""")
            val match = urlRegex.find(paragraph.text)
            match?.let {
                try { uriHandler.openUri(it.value) } catch (_: Exception) {}
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MarkdownBadgeRowView(badgeRow: MarkdownBlock.BadgeRow) {
    val uriHandler = LocalUriHandler.current

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        badgeRow.badges.forEach { badge ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (badge.linkUrl.isNotBlank()) {
                            try { uriHandler.openUri(badge.linkUrl) } catch (_: Exception) {}
                        }
                    }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(badge.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = badge.altText,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.heightIn(max = 28.dp)
                )
            }
        }
    }
}

@Composable
private fun MarkdownAdmonitionView(admonition: MarkdownBlock.Admonition) {
    val (accentColor, containerBg, icon) = when (admonition.type) {
        AdmonitionType.NOTE -> Triple(CyanAccentDark, Color(0xFF0C2433), Icons.Outlined.Info)
        AdmonitionType.TIP -> Triple(EmeraldAccentDark, Color(0xFF082F22), Icons.Outlined.Lightbulb)
        AdmonitionType.IMPORTANT -> Triple(Color(0xFFA855F7), Color(0xFF28133C), Icons.Outlined.CheckCircle)
        AdmonitionType.WARNING -> Triple(AmberAccentDark, Color(0xFF332007), Icons.Outlined.Warning)
        AdmonitionType.CAUTION -> Triple(RoseAccentDark, Color(0xFF380C16), Icons.Outlined.Dangerous)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SubCardShape,
        color = containerBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = admonition.type.name,
                    fontFamily = GoogleSansFlexFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = renderInlineFormatting(admonition.content),
                    fontFamily = GoogleSansFlexFamily,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = Color(0xFFE6EDF3)
                )
            }
        }
    }
}

@Composable
private fun MarkdownTableView(table: MarkdownBlock.Table) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SubCardShape,
        color = Color(0xFF161B22),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .background(Color(0xFF21262D))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                table.headers.forEach { header ->
                    Text(
                        text = header,
                        fontFamily = GoogleSansFlexFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TerracottaPrimaryDark,
                        modifier = Modifier.width(130.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x228B949E))
            )

            // Rows with alternating colors
            table.rows.forEachIndexed { index, row ->
                val rowBg = if (index % 2 == 0) Color(0xFF161B22) else Color(0xFF1B2028)
                Row(
                    modifier = Modifier
                        .background(rowBg)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    row.forEach { cell ->
                        Text(
                            text = renderInlineFormatting(cell),
                            fontFamily = GoogleSansFlexFamily,
                            fontSize = 12.5.sp,
                            color = Color(0xFFE6EDF3),
                            modifier = Modifier.width(130.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownTaskItemView(item: MarkdownBlock.TaskItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = null,
            tint = if (item.isChecked) EmeraldAccentDark else Color(0xFF8B949E),
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = renderInlineFormatting(item.text),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = GoogleSansFlexFamily,
                color = if (item.isChecked) Color(0xFF8B949E) else Color(0xFFE6EDF3),
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
            ),
            modifier = Modifier.weight(1f)
        )
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
                .padding(top = 8.dp, end = 10.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(TerracottaPrimaryDark)
        )
        Text(
            text = renderInlineFormatting(item.text),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = GoogleSansFlexFamily,
                color = Color(0xFFE6EDF3),
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
        color = Color(0xFF1B2129),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1F8B949E))
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TerracottaPrimaryDark)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = renderInlineFormatting(quote.text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = GoogleSansFlexFamily,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF8B949E),
                    lineHeight = 20.sp
                )
            )
        }
    }
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
        color = Color(0xFF0D1117),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x2E8B949E))
    ) {
        Column {
            // Header Bar: Language Badge + Touch-Friendly Copy Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Language Pill
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(TerracottaPrimaryDark.copy(alpha = 0.15f))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
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
                        .background(if (isCopied) EmeraldAccentDark.copy(alpha = 0.2f) else Color(0x1AFFFFFF))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (isCopied) EmeraldAccentDark else Color(0xFFE6EDF3),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isCopied) "Copied!" else "Copy",
                        style = CodeTypography.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCopied) EmeraldAccentDark else Color(0xFFE6EDF3)
                        )
                    )
                }
            }

            // Code Content with Syntax Highlighting and Horizontal Scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = renderSyntaxHighlightedCode(codeBlock.code, codeBlock.language),
                    style = CodeTypography.copy(
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        fontFamily = GoogleSansCodeFamily
                    )
                )
            }
        }
    }
}

@Composable
private fun RawMarkdownViewer(raw: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SubCardShape,
        color = Color(0xFF0D1117),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${raw.lines().size} lines • UTF-8",
                    fontFamily = GoogleSansCodeFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF8B949E)
                )
                ElevatedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(raw))
                        Toast.makeText(context, "Full markdown copied!", Toast.LENGTH_SHORT).show()
                    },
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF21262D)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All", fontSize = 11.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Text(
                    text = raw,
                    style = CodeTypography.copy(
                        fontFamily = GoogleSansCodeFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF8B949E)
                    )
                )
            }
        }
    }
}

// Sealed hierarchy for rich parsed Markdown elements
sealed interface MarkdownBlock {
    data class Header(val level: Int, val text: String) : MarkdownBlock
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock
    data class BadgeRow(val badges: List<BadgeItem>) : MarkdownBlock
    data class Admonition(val type: AdmonitionType, val content: String) : MarkdownBlock
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock
    data class TaskItem(val isChecked: Boolean, val text: String) : MarkdownBlock
    data class ListItem(val text: String) : MarkdownBlock
    data class BlockQuote(val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    object Divider : MarkdownBlock
}

enum class AdmonitionType { NOTE, TIP, IMPORTANT, WARNING, CAUTION }

data class BadgeItem(val altText: String, val imageUrl: String, val linkUrl: String)

/**
 * Intelligent Markdown tokenizer that parses headers, tables, badges, callouts, and lists.
 */
private fun parseMarkdownBlocks(raw: String): List<MarkdownBlock> {
    val lines = raw.lines()
    val blocks = mutableListOf<MarkdownBlock>()
    var inCodeBlock = false
    var codeLang = ""
    val codeLines = mutableListOf<String>()

    var inTable = false
    val tableHeaders = mutableListOf<String>()
    val tableRows = mutableListOf<List<String>>()

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trimEnd()

        // Handle Code Blocks ```
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
            i++
            continue
        }

        if (inCodeBlock) {
            codeLines.add(line)
            i++
            continue
        }

        val stripped = trimmed.trim()

        if (stripped.isEmpty()) {
            if (inTable && tableHeaders.isNotEmpty()) {
                blocks.add(MarkdownBlock.Table(tableHeaders.toList(), tableRows.toList()))
                tableHeaders.clear()
                tableRows.clear()
                inTable = false
            }
            i++
            continue
        }

        // Table Detection: Line starts and ends with |
        if (stripped.startsWith("|") && stripped.endsWith("|")) {
            val cells = stripped.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            if (!inTable) {
                // Header line
                inTable = true
                tableHeaders.clear()
                tableHeaders.addAll(cells)
            } else if (cells.all { it.all { ch -> ch == '-' || ch == ':' } }) {
                // Separator line |---|---| -> skip
            } else {
                tableRows.add(cells)
            }
            i++
            continue
        } else if (inTable) {
            blocks.add(MarkdownBlock.Table(tableHeaders.toList(), tableRows.toList()))
            tableHeaders.clear()
            tableRows.clear()
            inTable = false
        }

        // Dividers --- or ***
        if (stripped.startsWith("---") || stripped.startsWith("***") || stripped.startsWith("___")) {
            blocks.add(MarkdownBlock.Divider)
            i++
            continue
        }

        // Admonition / Callouts: > [!NOTE], > [!TIP], etc.
        if (stripped.startsWith("> [!") && stripped.contains("]")) {
            val typeStr = stripped.substringAfter("> [!").substringBefore("]").uppercase()
            val type = when (typeStr) {
                "TIP" -> AdmonitionType.TIP
                "IMPORTANT" -> AdmonitionType.IMPORTANT
                "WARNING" -> AdmonitionType.WARNING
                "CAUTION" -> AdmonitionType.CAUTION
                else -> AdmonitionType.NOTE
            }
            // Gather consecutive > lines
            val quoteLines = mutableListOf<String>()
            var j = i + 1
            while (j < lines.size && lines[j].trim().startsWith(">")) {
                quoteLines.add(lines[j].trim().removePrefix(">").trim())
                j++
            }
            blocks.add(MarkdownBlock.Admonition(type, quoteLines.joinToString(" ")))
            i = j
            continue
        }

        // Headers
        if (stripped.startsWith("#### ")) {
            blocks.add(MarkdownBlock.Header(4, cleanHtml(stripped.removePrefix("#### "))))
            i++
            continue
        }
        if (stripped.startsWith("### ")) {
            blocks.add(MarkdownBlock.Header(3, cleanHtml(stripped.removePrefix("### "))))
            i++
            continue
        }
        if (stripped.startsWith("## ")) {
            blocks.add(MarkdownBlock.Header(2, cleanHtml(stripped.removePrefix("## "))))
            i++
            continue
        }
        if (stripped.startsWith("# ")) {
            blocks.add(MarkdownBlock.Header(1, cleanHtml(stripped.removePrefix("# "))))
            i++
            continue
        }

        // Checklists: - [x] or - [ ]
        if (stripped.startsWith("- [x] ") || stripped.startsWith("- [X] ")) {
            blocks.add(MarkdownBlock.TaskItem(true, cleanHtml(stripped.substring(6))))
            i++
            continue
        }
        if (stripped.startsWith("- [ ] ")) {
            blocks.add(MarkdownBlock.TaskItem(false, cleanHtml(stripped.substring(6))))
            i++
            continue
        }

        // Badges Detection: [![badge](img)](link) or ![badge](img)
        val badgePattern = Regex("""\[?!\[(.*?)\]\((.*?)\)\]?\(?(.*?)\)?""")
        if (badgePattern.containsMatchIn(stripped) && (stripped.contains("shields.io") || stripped.contains("badge") || stripped.startsWith("[!["))) {
            val badgeList = mutableListOf<BadgeItem>()
            badgePattern.findAll(stripped).forEach { match ->
                val alt = match.groupValues.getOrNull(1) ?: ""
                val img = match.groupValues.getOrNull(2) ?: ""
                val link = match.groupValues.getOrNull(3) ?: ""
                if (img.startsWith("http")) {
                    badgeList.add(BadgeItem(alt, img, link))
                }
            }
            if (badgeList.isNotEmpty()) {
                blocks.add(MarkdownBlock.BadgeRow(badgeList))
                i++
                continue
            }
        }

        // List item
        if (stripped.startsWith("- ") || stripped.startsWith("* ") || stripped.startsWith("• ")) {
            val itemText = stripped.removePrefix("- ").removePrefix("* ").removePrefix("• ")
            blocks.add(MarkdownBlock.ListItem(cleanHtml(itemText)))
            i++
            continue
        }

        // Blockquote
        if (stripped.startsWith("> ")) {
            blocks.add(MarkdownBlock.BlockQuote(cleanHtml(stripped.removePrefix("> "))))
            i++
            continue
        }

        // Standard Paragraph
        val cleaned = cleanHtml(stripped)
        if (cleaned.isNotBlank()) {
            blocks.add(MarkdownBlock.Paragraph(cleaned))
        }
        i++
    }

    if (inCodeBlock && codeLines.isNotEmpty()) {
        blocks.add(MarkdownBlock.CodeBlock(codeLang, codeLines.joinToString("\n")))
    }
    if (inTable && tableHeaders.isNotEmpty()) {
        blocks.add(MarkdownBlock.Table(tableHeaders, tableRows))
    }

    return blocks
}

/**
 * Strips raw HTML wrappers common in README headers without destroying content.
 */
private fun cleanHtml(text: String): String {
    return text
        .replace(Regex("""<div[^>]*>"""), "")
        .replace("</div>", "")
        .replace(Regex("""<p[^>]*>"""), "")
        .replace("</p>", "")
        .replace("<br>", "\n")
        .replace("<br/>", "\n")
        .replace("<br />", "\n")
        .replace(Regex("""<img[^>]*src=["'](.*?)["'][^>]*>"""), " [Image: $1] ")
        .replace("<b>", "**")
        .replace("</b>", "**")
        .trim()
}

/**
 * Rich inline formatting for bold, italics, inline code, and links.
 */
private fun renderInlineFormatting(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        // Match: [label](url), **bold**, `code`, *italic*
        val pattern = Regex("""(\[([^\]]+)\]\(([^)]+)\)|\*\*([^*]+)\*\*|`([^`]+)`|\*([^*]+)\*)""")
        val matches = pattern.findAll(text)

        for (match in matches) {
            if (match.range.first > cursor) {
                append(text.substring(cursor, match.range.first))
            }

            val full = match.value
            when {
                // Markdown Link [text](url)
                full.startsWith("[") && full.contains("](") -> {
                    val label = match.groupValues[2]
                    pushStyle(
                        SpanStyle(
                            color = CyanAccentDark,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                    append(label)
                    pop()
                }
                // Bold **text**
                full.startsWith("**") && full.endsWith("**") -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TerracottaPrimaryDark))
                    append(match.groupValues[4])
                    pop()
                }
                // Inline Code `text`
                full.startsWith("`") && full.endsWith("`") -> {
                    pushStyle(
                        SpanStyle(
                            fontFamily = GoogleSansCodeFamily,
                            color = Color(0xFF7EE787),
                            background = Color(0x3321262D)
                        )
                    )
                    append(" " + match.groupValues[5] + " ")
                    pop()
                }
                // Italic *text*
                full.startsWith("*") && full.endsWith("*") -> {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = Color(0xFF8B949E)))
                    append(match.groupValues[6])
                    pop()
                }
                else -> append(full)
            }
            cursor = match.range.last + 1
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}

/**
 * Tokenizes code into keywords, strings, comments, numbers, and annotations for syntax styling.
 */
private fun renderSyntaxHighlightedCode(code: String, language: String): AnnotatedString {
    val keywords = setOf(
        "fun", "val", "var", "class", "interface", "object", "return", "if", "else", "when",
        "import", "package", "for", "while", "break", "continue", "throw", "try", "catch",
        "finally", "public", "private", "protected", "internal", "override", "sealed", "data",
        "const", "def", "export", "let", "function", "async", "await", "select", "from", "where"
    )

    return buildAnnotatedString {
        val lines = code.lines()
        lines.forEachIndexed { index, line ->
            var col = 0
            val lineLength = line.length

            while (col < lineLength) {
                // Comments
                if (col + 1 < lineLength && line.substring(col, col + 2) == "//" || line[col] == '#') {
                    pushStyle(SpanStyle(color = Color(0xFF8B949E), fontStyle = FontStyle.Italic))
                    append(line.substring(col))
                    pop()
                    break
                }

                // Strings "..." or '...'
                if (line[col] == '"' || line[col] == '\'') {
                    val quote = line[col]
                    val endQuote = line.indexOf(quote, col + 1)
                    val str = if (endQuote != -1) line.substring(col, endQuote + 1) else line.substring(col)
                    pushStyle(SpanStyle(color = Color(0xFFA5D6FF)))
                    append(str)
                    pop()
                    col += str.length
                    continue
                }

                // Annotations @Composable, @Inject
                if (line[col] == '@') {
                    val end = line.indexOfAny(charArrayOf(' ', '(', '\n'), col).takeIf { it != -1 } ?: lineLength
                    pushStyle(SpanStyle(color = AmberAccentDark, fontWeight = FontWeight.Bold))
                    append(line.substring(col, end))
                    pop()
                    col = end
                    continue
                }

                // Words (Keywords, Identifiers)
                if (line[col].isLetter() || line[col] == '_') {
                    val start = col
                    while (col < lineLength && (line[col].isLetterOrDigit() || line[col] == '_')) {
                        col++
                    }
                    val word = line.substring(start, col)
                    if (word in keywords) {
                        pushStyle(SpanStyle(color = Color(0xFFFF7B72), fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    } else if (word.first().isUpperCase()) {
                        pushStyle(SpanStyle(color = Color(0xFFD2A8FF)))
                        append(word)
                        pop()
                    } else {
                        pushStyle(SpanStyle(color = Color(0xFFE6EDF3)))
                        append(word)
                        pop()
                    }
                    continue
                }

                // Numbers
                if (line[col].isDigit()) {
                    val start = col
                    while (col < lineLength && (line[col].isLetterOrDigit() || line[col] == '.')) {
                        col++
                    }
                    pushStyle(SpanStyle(color = Color(0xFF79C0FF)))
                    append(line.substring(start, col))
                    pop()
                    continue
                }

                // Regular character
                pushStyle(SpanStyle(color = Color(0xFFE6EDF3)))
                append(line[col])
                pop()
                col++
            }

            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}
