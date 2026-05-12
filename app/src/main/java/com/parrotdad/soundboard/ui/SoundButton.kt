package com.parrotdad.soundboard.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A single square soundboard button with bounce animation and ripple feedback.
 *
 * @param emoji           The emoji displayed prominently in the centre.
 * @param label           Short text label shown below the emoji.
 * @param backgroundColor Pastel card background colour.
 * @param onClick         Called when the button is tapped (play mode).
 * @param editMode        When true, tapping opens the record dialog instead of playing.
 * @param hasCustomSound  Whether a custom recording exists for this button.
 * @param onEditClick     Called when the button is tapped in edit mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundButton(
    emoji: String,
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    editMode: Boolean = false,
    hasCustomSound: Boolean = false,
    onEditClick: () -> Unit = {}
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

    Box(modifier = modifier.aspectRatio(1f)) {
        Card(
            onClick = if (editMode) onEditClick else onClick,
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .semantics {
                    contentDescription = if (editMode)
                        "Edit $label sound"
                    else
                        "Play $label reminder"
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 1.dp
            ),
            interactionSource = interactionSource
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = emoji,
                    fontSize = 42.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF3A3A3A),
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Badge: mic icon in edit mode; green dot when custom sound exists
        if (editMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (hasCustomSound) Color(0xFF43A047) else Color(0xFF6750A4)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (hasCustomSound) "✓" else "🎙",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else if (hasCustomSound) {
            // Subtle green dot in normal mode to indicate a custom sound exists
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF43A047))
            )
        }
    }
}
