package io.gitforge.feature.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import io.gitforge.core.designsystem.theme.SyntaxComment
import io.gitforge.core.designsystem.theme.SyntaxFunction
import io.gitforge.core.designsystem.theme.SyntaxKeyword
import io.gitforge.core.designsystem.theme.SyntaxNumber
import io.gitforge.core.designsystem.theme.SyntaxString
import io.gitforge.core.designsystem.theme.SyntaxType

object SyntaxHighlighter {

    private val KOTLIN_KEYWORDS = setOf(
        "package", "import", "class", "interface", "object", "val", "var", "fun",
        "return", "if", "else", "when", "for", "while", "do", "break", "continue",
        "try", "catch", "finally", "throw", "null", "true", "false", "this", "super",
        "is", "as", "in", "override", "public", "private", "protected", "internal",
        "open", "sealed", "data", "abstract", "suspend", "inline", "const", "lateinit"
    )

    private val PYTHON_KEYWORDS = setOf(
        "def", "class", "import", "from", "as", "return", "if", "elif", "else",
        "for", "while", "break", "continue", "try", "except", "finally", "raise",
        "with", "yield", "lambda", "pass", "None", "True", "False", "and", "or", "not", "is", "in"
    )

    private val JS_KEYWORDS = setOf(
        "function", "const", "let", "var", "return", "if", "else", "switch", "case",
        "break", "default", "for", "while", "do", "try", "catch", "finally", "throw",
        "class", "extends", "new", "this", "super", "import", "export", "from", "async", "await",
        "null", "undefined", "true", "false"
    )

    fun highlight(code: String, fileExtension: String): AnnotatedString {
        val ext = fileExtension.lowercase()
        val keywords = when (ext) {
            "kt", "kts", "java" -> KOTLIN_KEYWORDS
            "py" -> PYTHON_KEYWORDS
            "js", "ts", "jsx", "tsx" -> JS_KEYWORDS
            else -> KOTLIN_KEYWORDS
        }

        return buildAnnotatedString {
            append(code)

            // 1. Strings: "..." or '...'
            val stringRegex = Regex("""(".*?"|'.*?')""")
            for (match in stringRegex.findAll(code)) {
                addStyle(SpanStyle(color = SyntaxString), match.range.first, match.range.last + 1)
            }

            // 2. Comments: //... or #...
            val commentRegex = if (ext == "py") Regex("""(#.*)""") else Regex("""(//.*|/\*[\s\S]*?\*/)""")
            for (match in commentRegex.findAll(code)) {
                addStyle(SpanStyle(color = SyntaxComment, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
            }

            // 3. Numbers
            val numberRegex = Regex("""\b(\d+(\.\d+)?)\b""")
            for (match in numberRegex.findAll(code)) {
                addStyle(SpanStyle(color = SyntaxNumber), match.range.first, match.range.last + 1)
            }

            // 4. Keywords
            val wordRegex = Regex("""\b([A-Za-z_][A-Za-z0-9_]*)\b""")
            for (match in wordRegex.findAll(code)) {
                val word = match.value
                if (keywords.contains(word)) {
                    addStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                } else if (word.first().isUpperCase()) {
                    addStyle(SpanStyle(color = SyntaxType), match.range.first, match.range.last + 1)
                }
            }
        }
    }
}
