package com.parrotdad.soundboard.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.parrotdad.soundboard.audio.RecordingManager
import com.parrotdad.soundboard.audio.SoundPlayer

enum class RecordState { IDLE, RECORDING, RECORDED }

/**
 * Modal dialog for recording a custom sound for a single soundboard button.
 *
 * @param itemKey       Stable key for the sound item (used for file naming).
 * @param itemLabel     Human-readable label shown in the dialog title.
 * @param hasExisting   Whether a custom recording already exists.
 * @param onSave        Called with the saved file path when the user confirms.
 * @param onRevert      Called when the user chooses to revert to the default sound.
 * @param onDismiss     Called when the dialog is dismissed without saving.
 */
@Composable
fun RecordDialog(
    itemKey: String,
    itemLabel: String,
    hasExisting: Boolean,
    onSave: (path: String) -> Unit,
    onRevert: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val recordingManager = remember { RecordingManager() }
    val soundPlayer = remember { SoundPlayer() }

    var recordState by remember { mutableStateOf(RecordState.IDLE) }
    var previewPath by remember { mutableStateOf<String?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDenied = false
            previewPath = recordingManager.startRecording(context, itemKey)
            recordState = RecordState.RECORDING
        } else {
            permissionDenied = true
        }
    }

    // Clean up on dismiss
    DisposableEffect(Unit) {
        onDispose {
            recordingManager.cancel()
            soundPlayer.stopAndRelease()
        }
    }

    Dialog(onDismissRequest = {
        recordingManager.cancel()
        soundPlayer.stopAndRelease()
        onDismiss()
    }) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Record \"$itemLabel\"",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = when {
                        permissionDenied -> "Microphone permission is required to record."
                        recordState == RecordState.IDLE && !hasExisting -> "Tap the mic to record your own voice."
                        recordState == RecordState.IDLE && hasExisting -> "You have a custom recording. Re-record or revert to default."
                        recordState == RecordState.RECORDING -> "Recording… tap stop when done."
                        else -> "Preview your recording, then save or try again."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                // Big mic / stop / preview button
                RecordButton(
                    state = recordState,
                    onStartRecord = {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPerm) {
                            previewPath = recordingManager.startRecording(context, itemKey)
                            recordState = RecordState.RECORDING
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    onStopRecord = {
                        recordingManager.stopRecording()
                        recordState = RecordState.RECORDED
                    },
                    onPreview = {
                        soundPlayer.playCustomOrFallback(
                            context,
                            RecordingManager.tempFileFor(context, itemKey).absolutePath,
                            0
                        )
                    }
                )

                Spacer(Modifier.height(24.dp))

                // Action row
                if (recordState == RecordState.RECORDED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                soundPlayer.stopAndRelease()
                                recordState = RecordState.IDLE
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Re-record") }

                        Button(
                            onClick = {
                                soundPlayer.stopAndRelease()
                                val saved = RecordingManager.commitRecording(context, itemKey)
                                if (saved != null) onSave(saved)
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        recordingManager.cancel()
                        soundPlayer.stopAndRelease()
                        onDismiss()
                    }) { Text("Cancel") }

                    if (hasExisting) {
                        TextButton(
                            onClick = {
                                soundPlayer.stopAndRelease()
                                RecordingManager.deleteCustomRecording(context, itemKey)
                                onRevert()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Use Default") }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordButton(
    state: RecordState,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onPreview: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "pulse_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = when (state) {
            RecordState.IDLE     -> Color(0xFF6750A4)
            RecordState.RECORDING -> Color(0xFFE53935)
            RecordState.RECORDED  -> Color(0xFF43A047)
        },
        label = "btn_color"
    )

    val (icon, description) = when (state) {
        RecordState.IDLE      -> "🎙️" to "Start recording"
        RecordState.RECORDING -> "⏹" to "Stop recording"
        RecordState.RECORDED  -> "▶️" to "Preview recording"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val onClickAction: () -> Unit = when (state) {
            RecordState.IDLE      -> onStartRecord
            RecordState.RECORDING -> onStopRecord
            RecordState.RECORDED  -> onPreview
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .scale(if (state == RecordState.RECORDING) pulse else 1f)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(onClick = onClickAction)
        ) {
            Text(
                text = icon,
                fontSize = 34.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
