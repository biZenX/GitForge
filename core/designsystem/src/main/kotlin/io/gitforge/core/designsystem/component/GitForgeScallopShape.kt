package io.gitforge.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic Material 3 Expressive Scallop / Flower Shape (12 lobes).
 * Replicated directly from Wafeer and Essentials UI specifications.
 */
class ScallopedShape(
    private val lobes: Int = 12,
    private val amplitudeRatio: Float = 0.10f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = minOf(centerX, centerY) * (1f - amplitudeRatio)
        val amplitude = minOf(centerX, centerY) * amplitudeRatio

        val steps = lobes * 16
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps.toFloat()) * 2f * PI.toFloat()
            val r = baseRadius + amplitude * cos(lobes * theta)
            val x = centerX + r * cos(theta)
            val y = centerY + r * sin(theta)
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

/**
 * Wafeer-Style Scalloped Flower Badge with circular progress arc and count.
 * Seen in Wafeer (photo_2026-10-15_19-29-15.jpg).
 */
@Composable
fun WafeerFlowerBadge(
    countText: String,
    labelText: String,
    modifier: Modifier = Modifier,
    progress: Float = 0.85f,
    badgeColor: Color = Color(0xFF1E3823),
    borderColor: Color = Color(0xFF86EFAC),
    arcColor: Color = Color(0xFF86EFAC),
    trackColor: Color = Color(0x3386EFAC),
    onClick: () -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "BadgeProgress"
    )

    Box(
        modifier = modifier
            .size(104.dp)
            .bounceClick(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Outer circular progress arc
        Canvas(modifier = Modifier.size(104.dp)) {
            val strokeWidth = 3.5.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Arc
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner Scalloped Flower
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(ScallopedShape(lobes = 12, amplitudeRatio = 0.12f))
                .background(badgeColor)
                .border(2.dp, borderColor, ScallopedShape(lobes = 12, amplitudeRatio = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = countText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = labelText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD1D5DB)
                )
            }
        }
    }
}

/**
 * Wavy Sine-Wave Divider ("يافطة الخط المتعرج").
 * Direct replica of the wavy squiggly divider seen in Wafeer What's New video.
 * Format: 〰〰〰 Text 〰〰〰
 */
@Composable
fun WavyDivider(
    modifier: Modifier = Modifier,
    text: String? = null,
    waveColor: Color = Color(0xFF4B5563),
    textColor: Color = Color(0xFF9CA3AF),
    waveHeight: Dp = 10.dp,
    wavelength: Float = 28f
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(waveHeight)
        ) {
            drawSineWave(waveColor, wavelength)
        }

        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(waveHeight)
            ) {
                drawSineWave(waveColor, wavelength)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSineWave(
    color: Color,
    wavelength: Float
) {
    val path = Path()
    val midY = size.height / 2f
    val amplitude = size.height / 2.5f

    path.moveTo(0f, midY)
    var x = 0f
    while (x <= size.width) {
        val y = midY + amplitude * sin((x / wavelength) * 2f * PI.toFloat())
        path.lineTo(x, y)
        x += 2f
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
    )
}
