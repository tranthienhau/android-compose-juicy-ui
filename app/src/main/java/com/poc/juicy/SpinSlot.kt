package com.poc.juicy

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun SpinSlot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vibrator: Vibrator? = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Vibrator::class.java) as? Vibrator)
                ?: (context.getSystemService(VibratorManager::class.java))?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var score by remember { mutableStateOf(0) }
    var winMessage by remember { mutableStateOf<String?>(null) }
    var spinning by remember { mutableStateOf(false) }
    val reelA = remember { Animatable(0f) }
    val reelB = remember { Animatable(0f) }
    val reelC = remember { Animatable(0f) }
    val winShake = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<Particle>() }

    LaunchedEffect(spinning) {
        if (spinning) {
            val targetA = Random.nextInt(3, 6) * 360f + Random.nextInt(0, 8) * 45f
            val targetB = Random.nextInt(4, 7) * 360f + Random.nextInt(0, 8) * 45f
            val targetC = Random.nextInt(5, 8) * 360f + Random.nextInt(0, 8) * 45f
            tickHaptic(vibrator)
            reelA.animateTo(reelA.value + targetA, tween(900, easing = LinearOutSlowInEasing))
            tickHaptic(vibrator)
            reelB.animateTo(reelB.value + targetB, tween(1100, easing = LinearOutSlowInEasing))
            tickHaptic(vibrator)
            reelC.animateTo(reelC.value + targetC, tween(1300, easing = LinearOutSlowInEasing))

            val symA = ((reelA.value / 45f).toInt() % 8 + 8) % 8
            val symB = ((reelB.value / 45f).toInt() % 8 + 8) % 8
            val symC = ((reelC.value / 45f).toInt() % 8 + 8) % 8
            val win = symA == symB && symB == symC
            if (win) {
                score += 100
                winMessage = "BIG WIN +100"
                winHaptic(vibrator)
                winShake.snapTo(1f)
                winShake.animateTo(
                    0f,
                    spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow)
                )
                repeat(40) {
                    particles += Particle(
                        x = 0.5f + Random.nextFloat() * 0.02f - 0.01f,
                        y = 0.55f,
                        vx = (Random.nextFloat() - 0.5f) * 0.04f,
                        vy = -Random.nextFloat() * 0.05f - 0.02f,
                        hue = Random.nextFloat() * 360f,
                        life = 1f
                    )
                }
            } else {
                winMessage = null
            }
            spinning = false
        }
    }

    LaunchedEffect(particles.size) {
        while (particles.isNotEmpty()) {
            kotlinx.coroutines.delay(16)
            val it = particles.listIterator()
            while (it.hasNext()) {
                val p = it.next()
                p.x += p.vx
                p.y += p.vy
                p.vy += 0.0018f
                p.life -= 0.018f
                if (p.life <= 0f) it.remove()
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Score: $score",
            color = Color(0xFF34D399),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .graphicsLayer {
                    val shake = winShake.value
                    translationX = (kotlin.math.sin(shake * 24f) * 12f)
                }
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E1B4B), Color(0xFF111827))
                    )
                )
                .padding(20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Reel(angle = reelA.value)
                Reel(angle = reelB.value)
                Reel(angle = reelC.value)
            }
            ParticleLayer(particles)
        }
        Spacer(Modifier.height(20.dp))
        winMessage?.let {
            Text(
                it,
                color = Color(0xFFFACC15),
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { if (!spinning) spinning = true },
            enabled = !spinning,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEC4899),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text(
                if (spinning) "SPINNING..." else "TAP TO SPIN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val hue: Float,
    var life: Float
)

private fun tickHaptic(v: Vibrator?) {
    v ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(8, VibrationEffect.DEFAULT_AMPLITUDE / 2))
    }
}

private fun winHaptic(v: Vibrator?) {
    v ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(0, 40, 60, 80, 60, 120),
                intArrayOf(0, 180, 0, 220, 0, 255),
                -1
            )
        )
    }
}
