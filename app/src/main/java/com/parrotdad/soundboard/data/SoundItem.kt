package com.parrotdad.soundboard.data

/**
 * Represents a single soundboard button entry.
 *
 * @param key       Stable identifier used to store/retrieve custom recordings (e.g. "teeth").
 * @param emoji     Emoji shown on the button.
 * @param label     Short text label shown below the emoji.
 * @param audioResId Default audio resource (played when no custom recording exists).
 */
data class SoundItem(
    val key: String,
    val emoji: String,
    val label: String,
    val audioResId: Int
)
