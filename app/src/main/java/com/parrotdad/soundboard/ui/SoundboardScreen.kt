package com.parrotdad.soundboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parrotdad.soundboard.audio.SoundPlayer
import com.parrotdad.soundboard.data.SoundItem
import com.parrotdad.soundboard.ui.theme.buttonColors

/**
 * The single screen of the app.
 * Manages [SoundPlayer] lifecycle via [DisposableEffect] so audio is
 * always cleaned up when the screen leaves composition (e.g. app backgrounded).
 */
@Composable
fun SoundboardScreen(items: List<SoundItem>) {
    val context = LocalContext.current
    val soundPlayer = remember { SoundPlayer() }

    // Release audio resources when this composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            soundPlayer.stopAndRelease()
        }
    }

    // Startup bounce for the whole grid
    val gridScale = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        gridScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8F0), Color(0xFFF0F4FF))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App title bar
            Text(
                text = "🦜 ParrotDad",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )

            // 3x3 grid with startup bounce
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .scale(gridScale.value)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    SoundButton(
                        emoji = item.emoji,
                        label = item.label,
                        backgroundColor = buttonColors[index % buttonColors.size],
                        onClick = {
                            triggerHaptic(context)
                            soundPlayer.play(context, item.audioResId)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Delivers a short, lightweight haptic tap using the appropriate API for the SDK version.
 */
private fun triggerHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.vibrate(
                VibrationEffect.createOneShot(30L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        }
    } catch (_: Exception) {
        // Haptic is a nice-to-have; swallow any errors silently
    }
}
