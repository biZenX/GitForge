package io.gitforge.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ExpressiveDarkColorScheme = darkColorScheme(
    primary = TerracottaPrimaryDark,
    onPrimary = Color(0xFF381404),
    primaryContainer = TerracottaContainerDark,
    onPrimaryContainer = TerracottaOnContainerDark,
    secondary = CyanAccentDark,
    onSecondary = Color(0xFF00364F),
    secondaryContainer = CyanContainerDark,
    onSecondaryContainer = Color(0xFFC7E7FF),
    tertiary = EmeraldAccentDark,
    onTertiary = Color(0xFF003823),
    tertiaryContainer = EmeraldContainerDark,
    onTertiaryContainer = EmeraldOnContainerDark,
    background = CharcoalBackgroundDark,
    onBackground = WarmOnSurfaceDark,
    surface = CharcoalSurfaceDark,
    onSurface = WarmOnSurfaceDark,
    surfaceVariant = CharcoalSurfaceContainerDark,
    onSurfaceVariant = WarmOnSurfaceVariantDark,
    surfaceContainerLowest = CharcoalSurfaceContainerLowestDark,
    surfaceContainerLow = CharcoalSurfaceContainerLowDark,
    surfaceContainer = CharcoalSurfaceContainerDark,
    surfaceContainerHigh = CharcoalSurfaceContainerHighDark,
    surfaceContainerHighest = CharcoalSurfaceContainerHighestDark,
    outline = WarmOutlineDark,
    outlineVariant = WarmOutlineVariantDark,
    error = RoseAccentDark,
    onError = Color(0xFF4C0012)
)

private val ExpressiveLightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF381404),
    secondary = CyanAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC7E7FF),
    onSecondaryContainer = Color(0xFF001E2E),
    tertiary = EmeraldAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFA7F3D0),
    onTertiaryContainer = Color(0xFF002113),
    background = WarmSandBackgroundLight,
    onBackground = WarmSandOnSurfaceLight,
    surface = WarmSandSurfaceLight,
    onSurface = WarmSandOnSurfaceLight,
    surfaceVariant = WarmSandSurfaceContainerLight,
    onSurfaceVariant = WarmSandOnSurfaceVariantLight,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAF7F3),
    surfaceContainer = WarmSandSurfaceContainerLight,
    surfaceContainerHigh = WarmSandSurfaceContainerHighLight,
    surfaceContainerHighest = Color(0xFFE5DDD7),
    outline = WarmSandOutlineLight,
    outlineVariant = Color(0xFFD6D3D1),
    error = RoseAccent,
    onError = Color.White
)

@Composable
fun GitForgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> ExpressiveDarkColorScheme
        else -> ExpressiveLightColorScheme
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
