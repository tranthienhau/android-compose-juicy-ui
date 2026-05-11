package com.poc.juicy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

private val symbols = listOf("7", "★", "◆", "♣", "♥", "♠", "$", "✦")
private val symbolColors = listOf(
    Color(0xFFEF4444), Color(0xFFFACC15), Color(0xFF22D3EE),
    Color(0xFF34D399), Color(0xFFEC4899), Color(0xFF8B5CF6),
    Color(0xFFF59E0B), Color(0xFF60A5FA)
)

@Composable
fun Reel(angle: Float) {
    val index = ((angle / 45f).toInt() % 8 + 8) % 8
    val symbol = symbols[index]
    val color = symbolColors[index]
    androidx.compose.foundation.layout.Box(
        Modifier
            .size(72.dp)
            .graphicsLayer {
                rotationZ = (angle % 360f) * 0.05f
                shadowElevation = 12f
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B)))
            )
    ) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val glowR = size.minDimension * 0.5f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(color.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = glowR
                ),
                radius = glowR
            )
        }
        androidx.compose.material3.Text(
            symbol,
            color = color,
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer { },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
            fontSize = androidx.compose.ui.unit.TextUnit.Unspecified,
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
        )
    }
}
