package com.parrotdad.soundboard.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A single square soundboard button with bounce animation and ripple feedback.
 *
 * @param emoji           The emoji displayed prominently in the centre.
 * @param backgroundColor Pastel card background colour.
 * @param onClick         Called when the button is tapped (play mode).
 * @param editMode        When true, tapping opens the record dialog instead of playing.
 * @param hasCustomSound  Whether a custom recording exists for this button.
 * @param hasCustomEmoji  Whether a custom emoji is set for this button.
 * @param repeatCount     How many times the sound plays consecutively (1–5).
 * @param onEditClick     Called when the button body is tapped in edit mode (record sound).
 * @param onEmojiClick    Called when the emoji badge is tapped in edit mode (change icon).
 * @param onRepeatClick   Called when the repeat badge is tapped in edit mode (cycle count).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundButton(
    emoji: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    hasCustomSound: Boolean = false,
    hasCustomEmoji: Boolean = false,
    repeatCount: Int = 1,
    squareAspect: Boolean = true,
    onEditClick: () -> Unit = {},
    onEmojiClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.91f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    // In edit mode dim the card slightly to signal it's editable
    val cardColor = if (editMode) backgroundColor.copy(alpha = 0.75f) else backgroundColor

    Box(modifier = if (squareAspect) modifier.aspectRatio(1f) else modifier.fillMaxWidth().height(88.dp)) {
        Card(
            onClick = if (editMode) onEditClick else onClick,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .semantics {
                    contentDescription = if (editMode) "Edit sound" else "Play sound"
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            ),
            interactionSource = interactionSource
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (editMode) {
            // Top-right: mic badge (tap = record sound)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (hasCustomSound) Color(0xFF43A047) else Color(0xFF6750A4))
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasCustomSound) "✓" else "🎙",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
            // Top-left: emoji badge (tap = change icon)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (hasCustomEmoji) Color(0xFFFF8F00) else Color(0xFF78909C))
                    .clickable(onClick = onEmojiClick),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasCustomEmoji) "✓" else "🖼",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else if (hasCustomSound || hasCustomEmoji) {
            // Subtle dot in normal mode to indicate customisation
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF43A047))
            )
        }

        // Bottom-left: repeat count badge — always visible, tappable in edit mode
        val repeatIsDefault = repeatCount <= 1
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (repeatIsDefault) Color(0x55000000) else Color(0xFFE65100)
                )
                .then(if (editMode) Modifier.clickable(onClick = onRepeatClick) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×$repeatCount",
                fontSize = 10.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
