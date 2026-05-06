package com.parrotdad.soundboard.data

/**
 * Represents a single soundboard button entry.
 */
data class SoundItem(
    val emoji: String,
    val label: String,
    val audioResId: Int
)
