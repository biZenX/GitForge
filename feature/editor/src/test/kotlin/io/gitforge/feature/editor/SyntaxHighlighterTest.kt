package io.gitforge.feature.editor

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SyntaxHighlighterTest {

    @Test
    fun `highlight applies styles to Kotlin keywords and strings`() {
        val code = """
            package com.example
            class User(val name: String)
        """.trimIndent()

        val annotated = SyntaxHighlighter.highlight(code, "kt")
        assertThat(annotated.text).isEqualTo(code)
        assertThat(annotated.spanStyles).isNotEmpty()
    }

    @Test
    fun `highlight handles empty and plain text safely`() {
        val empty = SyntaxHighlighter.highlight("", "txt")
        assertThat(empty.text).isEmpty()

        val plain = SyntaxHighlighter.highlight("just some plain notes", "md")
        assertThat(plain.text).isEqualTo("just some plain notes")
    }
}
