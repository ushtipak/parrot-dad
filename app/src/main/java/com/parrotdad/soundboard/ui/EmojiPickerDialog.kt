package com.parrotdad.soundboard.ui

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val EMOJI_OPTIONS = listOf(
    // Hygiene & morning
    "🪥", "🧻", "🧼", "🚿", "🛁", "🪒", "🧴", "🪮",
    // Clothes & shoes
    "👟", "👕", "👖", "🧢", "🧤", "🧦", "👗", "🥾",
    // Food & eating
    "🍽️", "🥄", "🍴", "🥛", "🍎", "🥦", "🧃", "🍞",
    // Tidying & chores
    "🧸", "📦", "🗑️", "🧹", "🪣", "🛒", "📚", "✏️",
    // Screen / devices
    "❌", "📺", "📱", "💻", "🎮", "🔇", "⏹️", "🚫",
    // Manners & feelings
    "🙏", "🤫", "😴", "😊", "🤗", "👍", "❤️", "🌟",
    // Bedtime & sleep
    "🛏️", "🌙", "💤", "🌛", "🧸", "📖", "🕯️", "🌃",
    // Animals & fun
    "🦜", "🐶", "🐱", "🐸", "🦋", "🌈", "⭐", "🎉",
)

/**
 * Dialog that lets the user pick a replacement emoji for a soundboard button.
 *
 * @param currentEmoji  The emoji currently shown on the button.
 * @param defaultEmoji  The original default emoji (shown for the reset option).
 * @param onPick        Called with the chosen emoji.
 * @param onReset       Called when the user taps "Use Default".
 * @param onDismiss     Called when dismissed without picking.
 */
@Composable
fun EmojiPickerDialog(
    currentEmoji: String,
    defaultEmoji: String,
    onPick: (emoji: String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(currentEmoji) }
    val isCustomised = currentEmoji != defaultEmoji

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose an Icon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Tap an emoji to select it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(EMOJI_OPTIONS) { emoji ->
                        val isSelected = emoji == selected
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else Color.Transparent
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(8.dp)
                                    ) else Modifier
                                )
                                .clickable { selected = emoji }
                        ) {
                            Text(text = emoji, fontSize = 20.sp, textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    if (isCustomised) {
                        TextButton(
                            onClick = onReset,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Use Default") }
                    }

                    Button(
                        onClick = { onPick(selected) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Save") }
                }
            }
        }
    }
}
