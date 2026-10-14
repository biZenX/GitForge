package io.gitforge.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Expressive Shapes (Wafeer / Zenith caliber)
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Specific Expressive Shape Tokens
val HeroCardShape = RoundedCornerShape(28.dp)
val ItemCardShape = RoundedCornerShape(22.dp)
val SubCardShape = RoundedCornerShape(18.dp)
val IconContainerShape = RoundedCornerShape(14.dp)
val PillBadgeShape = RoundedCornerShape(9999.dp)
