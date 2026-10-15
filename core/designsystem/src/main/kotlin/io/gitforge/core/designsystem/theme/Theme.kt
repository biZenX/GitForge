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
    onPrimary = Color(0xFF2E1003),
    primaryContainer = Color(0xFF471C09),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = CyanAccentDark,
    onSecondary = Color(0xFF00354D),
    secondaryContainer = Color(0xFF08415C),
    onSecondaryContainer = Color(0xFFCBE6FF),
    tertiary = EmeraldAccentDark,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = Color(0xFF064E33),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = Color(0xFF0D1117),
    onBackground = Color(0xFFE6EDF3),
    surface = Color(0xFF161B22),
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = Color(0xFF21262D),
    onSurfaceVariant = Color(0xFF8B949E),
    surfaceContainerLowest = Color(0xFF090D12),
    surfaceContainerLow = Color(0xFF13171E),
    surfaceContainer = Color(0xFF161B22),
    surfaceContainerHigh = Color(0xFF21262D),
    surfaceContainerHighest = Color(0xFF30363D),
    outline = Color(0x338B949E),
    outlineVariant = Color(0x1F8B949E),
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
    dynamicColor: Boolean = false,
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
