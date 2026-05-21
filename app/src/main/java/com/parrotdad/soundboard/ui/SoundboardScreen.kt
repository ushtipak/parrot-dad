package com.parrotdad.soundboard.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.parrotdad.soundboard.data.UserPreferences
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

    var editMode by remember { mutableStateOf(false) }

    // Track which item is currently showing the record dialog
    var recordingItem by remember { mutableStateOf<SoundItem?>(null) }
    // Track which item is currently showing the emoji picker
    var emojiItem by remember { mutableStateOf<SoundItem?>(null) }

    // Map of key -> custom path (null = use default). Initialised from prefs.
    val customPaths = remember {
        mutableStateMapOf<String, String?>().also { map ->
            items.forEach { item ->
                val path = UserPreferences.getCustomPath(context, item.key)
                if (path != null) map[item.key] = path
            }
        }
    }

    // Map of key -> custom emoji (null = use default). Initialised from prefs.
    val customEmojis = remember {
        mutableStateMapOf<String, String?>().also { map ->
            items.forEach { item ->
                val emoji = UserPreferences.getCustomEmoji(context, item.key)
                if (emoji != null) map[item.key] = emoji
            }
        }
    }

    // Map of key -> repeat count (default 1). Initialised from prefs.
    val repeatCounts = remember {
        mutableStateMapOf<String, Int>().also { map ->
            items.forEach { item ->
                map[item.key] = UserPreferences.getRepeatCount(context, item.key)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { soundPlayer.stopAndRelease() }
    }

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

    val bgColor by animateColorAsState(
        targetValue = if (editMode) Color(0xFFFFF0F0) else Color(0xFFFFF8F0),
        label = "bg_top"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgColor, Color(0xFFF0F4FF))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Title bar + edit toggle ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🦜 ParrotDad",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                FilterChip(
                    selected = editMode,
                    onClick = {
                        editMode = !editMode
                        if (!editMode) soundPlayer.stopAndRelease()
                    },
                    label = { Text(if (editMode) "✓ Done" else "✏️ Edit") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Edit mode hint
            AnimatedVisibility(visible = editMode, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = "🖼 change icon  ·  🎙 record sound",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // ── 3×3 grid ──
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
                    val customPath = customPaths[item.key]
                    val customEmoji = customEmojis[item.key]
                    val repeat = repeatCounts[item.key] ?: 1
                    SoundButton(
                        emoji = customEmoji ?: item.emoji,
                        backgroundColor = buttonColors[index % buttonColors.size],
                        editMode = editMode,
                        hasCustomSound = customPath != null,
                        hasCustomEmoji = customEmoji != null,
                        repeatCount = repeat,
                        onClick = {
                            triggerHaptic(context)
                            soundPlayer.playCustomOrFallback(context, customPath, item.audioResId, repeat)
                        },
                        onEditClick = {
                            soundPlayer.stopAndRelease()
                            recordingItem = item
                        },
                        onEmojiClick = {
                            emojiItem = item
                        },
                        onRepeatClick = {
                            val next = if (repeat >= 5) 1 else repeat + 1
                            UserPreferences.setRepeatCount(context, item.key, next)
                            repeatCounts[item.key] = next
                        }
                    )
                }
            }
        }

        // ── Emoji picker dialog ──
        emojiItem?.let { item ->
            EmojiPickerDialog(
                currentEmoji = customEmojis[item.key] ?: item.emoji,
                defaultEmoji = item.emoji,
                onPick = { chosen ->
                    UserPreferences.setCustomEmoji(context, item.key, chosen)
                    customEmojis[item.key] = chosen
                    emojiItem = null
                },
                onReset = {
                    UserPreferences.clearCustomEmoji(context, item.key)
                    customEmojis.remove(item.key)
                    emojiItem = null
                },
                onDismiss = { emojiItem = null }
            )
        }

        // ── Record dialog ──
        recordingItem?.let { item ->
            RecordDialog(
                itemKey = item.key,
                itemLabel = item.key,
                hasExisting = customPaths[item.key] != null,
                onSave = { savedPath ->
                    UserPreferences.setCustomPath(context, item.key, savedPath)
                    customPaths[item.key] = savedPath
                    recordingItem = null
                },
                onRevert = {
                    UserPreferences.clearCustomPath(context, item.key)
                    customPaths.remove(item.key)
                    recordingItem = null
                },
                onDismiss = { recordingItem = null }
            )
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
