package io.gitforge.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Expressive M3 Tonal Palette (Inspired by sameerasw / Wafeer)
val TerracottaPrimary = Color(0xFFD46238)
val TerracottaPrimaryDark = Color(0xFFFF9E79)
val TerracottaContainerDark = Color(0xFF4A1E0E)
val TerracottaOnContainerDark = Color(0xFFFFDBCF)

val EmeraldAccent = Color(0xFF10B981)
val EmeraldAccentDark = Color(0xFF34D399)
val EmeraldContainerDark = Color(0xFF064E3B)
val EmeraldOnContainerDark = Color(0xFFA7F3D0)

val AmberAccent = Color(0xFFF59E0B)
val AmberAccentDark = Color(0xFFFBBF24)
val AmberContainerDark = Color(0xFF78350F)

val CyanAccent = Color(0xFF0284C7)
val CyanAccentDark = Color(0xFF38BDF8)
val CyanContainerDark = Color(0xFF0C4A6E)

val RoseAccent = Color(0xFFE11D48)
val RoseAccentDark = Color(0xFFFB7185)
val RoseContainerDark = Color(0xFF881337)

// Deep Tonal Charcoal / Warm Slate Surfaces
val CharcoalBackgroundDark = Color(0xFF0E1013)
val CharcoalSurfaceDark = Color(0xFF14171C)
val CharcoalSurfaceContainerLowestDark = Color(0xFF090B0E)
val CharcoalSurfaceContainerLowDark = Color(0xFF181B21)
val CharcoalSurfaceContainerDark = Color(0xFF1E222A)
val CharcoalSurfaceContainerHighDark = Color(0xFF252A33)
val CharcoalSurfaceContainerHighestDark = Color(0xFF2F3540)

val WarmOnSurfaceDark = Color(0xFFF1F5F9)
val WarmOnSurfaceVariantDark = Color(0xFF94A3B8)
val WarmOutlineDark = Color(0x2EFFFFFF)
val WarmOutlineVariantDark = Color(0x1AFFFFFF)

// Light Palette (Warm Sand / Ivory)
val WarmSandBackgroundLight = Color(0xFFFDFBF7)
val WarmSandSurfaceLight = Color(0xFFFFFFFF)
val WarmSandSurfaceContainerLight = Color(0xFFF4EEEA)
val WarmSandSurfaceContainerHighLight = Color(0xFFEDE4DF)
val WarmSandOnSurfaceLight = Color(0xFF1C1917)
val WarmSandOnSurfaceVariantLight = Color(0xFF57534E)
val WarmSandOutlineLight = Color(0xFFE7E5E4)

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

// Syntax Highlighting Tokens
val SyntaxKeyword = Color(0xFFFF7B72)
val SyntaxString = Color(0xFFA5D6FF)
val SyntaxComment = Color(0xFF8B949E)
val SyntaxFunction = Color(0xFFD2A8FF)
val SyntaxNumber = Color(0xFF79C0FF)
val SyntaxType = Color(0xFFFFA657)

// Git Diff Colors
val DiffAddBg = Color(0x2634D399)
val DiffAddText = Color(0xFF34D399)
val DiffDeleteBg = Color(0x26FB7185)
val DiffDeleteText = Color(0xFFFB7185)
val DiffHeaderBg = Color(0x1AFF9E79)

// Status Colors
val StatusSuccess = EmeraldAccentDark
val StatusError = RoseAccentDark
val StatusWarning = AmberAccentDark
val StatusInfo = CyanAccentDark
val StatusMerged = Color(0xFFA855F7)
val StatusDraft = Color(0xFF94A3B8)
