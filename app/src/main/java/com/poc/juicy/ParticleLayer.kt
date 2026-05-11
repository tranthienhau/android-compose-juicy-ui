package com.poc.juicy

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun ParticleLayer(particles: List<Particle>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val color = Color.hsv(p.hue, 0.85f, 1f, p.life.coerceIn(0f, 1f))
            drawCircle(
                color = color,
                radius = 6f + (1f - p.life) * 4f,
                center = Offset(p.x * size.width, p.y * size.height)
            )
        }
    }
}
