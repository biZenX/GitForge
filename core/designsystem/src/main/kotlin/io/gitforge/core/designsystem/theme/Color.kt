package io.gitforge.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Expressive M3 Tonal Palette (Essentials, Respiro & Wafeer Caliber - Zero Neon)
val TerracottaPrimary = Color(0xFF2B5B9E)
val TerracottaPrimaryDark = Color(0xFFA8C7FA) // Soothing Material You Periwinkle Blue
val TerracottaContainerDark = Color(0xFF1D3B6A)
val TerracottaOnContainerDark = Color(0xFFD3E3FD)

val EmeraldAccent = Color(0xFF2E7D32)
val EmeraldAccentDark = Color(0xFF81C784) // Soft Pastel Mint Green
val EmeraldContainerDark = Color(0xFF1B4D20)
val EmeraldOnContainerDark = Color(0xFFC8E6C9)

val AmberAccent = Color(0xFFB78103)
val AmberAccentDark = Color(0xFFFFE082) // Soft Warm Gold
val AmberContainerDark = Color(0xFF5D4000)

val CyanAccent = Color(0xFF1976D2)
val CyanAccentDark = Color(0xFF90CAF9) // Soft Sky Blue
val CyanContainerDark = Color(0xFF0D47A1)

val RoseAccent = Color(0xFFC2185B)
val RoseAccentDark = Color(0xFFE57373) // Soft Coral Rose
val RoseContainerDark = Color(0xFF5C001E)

// Soft Lavender from Essentials
val LavenderAccent = Color(0xFF6750A4)
val LavenderAccentDark = Color(0xFFD0BCFF)
val LavenderContainerDark = Color(0xFF4F378B)

// Deep Matte Slate / Organic Obsidian Surfaces (Never pitch-black, zero neon glare)
val CharcoalBackgroundDark = Color(0xFF14161B)
val CharcoalSurfaceDark = Color(0xFF1B1E24)
val CharcoalSurfaceContainerLowestDark = Color(0xFF0F1115)
val CharcoalSurfaceContainerLowDark = Color(0xFF171920)
val CharcoalSurfaceContainerDark = Color(0xFF1E2128)
val CharcoalSurfaceContainerHighDark = Color(0xFF262A33)
val CharcoalSurfaceContainerHighestDark = Color(0xFF2E333E)

val WarmOnSurfaceDark = Color(0xFFEDF0F5)
val WarmOnSurfaceVariantDark = Color(0xFF9AA0A6)
val WarmOutlineDark = Color(0x1FFFFFFF)
val WarmOutlineVariantDark = Color(0x12FFFFFF)

// Light Palette (Warm Sand / Ivory)
val WarmSandBackgroundLight = Color(0xFFF9FAFB)
val WarmSandSurfaceLight = Color(0xFFFFFFFF)
val WarmSandSurfaceContainerLight = Color(0xFFF1F3F5)
val WarmSandSurfaceContainerHighLight = Color(0xFFE8EAED)
val WarmSandOnSurfaceLight = Color(0xFF1F2937)
val WarmSandOnSurfaceVariantLight = Color(0xFF4B5563)
val WarmSandOutlineLight = Color(0xFFE5E7EB)

// Backward-compatible tokens used across screens
val ForgeIndigo = TerracottaPrimary
val ForgeIndigoDark = TerracottaPrimaryDark
val ForgeCyan = CyanAccent
val ForgeCyanDark = CyanAccentDark
val ForgeEmerald = EmeraldAccent
val ForgeEmeraldDark = EmeraldAccentDark
val ForgeAmber = AmberAccent
val ForgeAmberDark = AmberAccentDark
val ForgeRose = RoseAccent
val ForgeRoseDark = RoseAccentDark

// Syntax Highlighting Tokens (Subtle, balanced, eye-friendly)
val SyntaxKeyword = Color(0xFFE57373)
val SyntaxString = Color(0xFF81C784)
val SyntaxComment = Color(0xFF757575)
val SyntaxFunction = Color(0xFF90CAF9)
val SyntaxNumber = Color(0xFFFFB74D)
val SyntaxType = Color(0xFFBA68C8)

// Git Diff Colors
val DiffAddBg = Color(0x1F81C784)
val DiffAddText = Color(0xFF81C784)
val DiffDeleteBg = Color(0x1FE57373)
val DiffDeleteText = Color(0xFFE57373)
val DiffHeaderBg = Color(0x1FA8C7FA)

// Status Colors
val StatusSuccess = EmeraldAccentDark
val StatusError = RoseAccentDark
val StatusWarning = AmberAccentDark
val StatusInfo = CyanAccentDark
val StatusMerged = LavenderAccentDark
val StatusDraft = Color(0xFF9E9E9E)
