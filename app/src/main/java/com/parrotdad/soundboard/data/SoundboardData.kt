package com.parrotdad.soundboard.data

import com.parrotdad.soundboard.R

/**
 * Static list of 9 soundboard items.
 * Audio files are expected in res/raw/.
 */
val soundboardItems: List<SoundItem> = listOf(
    SoundItem(emoji = "🪥", label = "Teeth", audioResId = R.raw.sound_teeth),
    SoundItem(emoji = "🧻", label = "Towel", audioResId = R.raw.sound_towel),
    SoundItem(emoji = "👟", label = "Shoes", audioResId = R.raw.sound_shoes),
    SoundItem(emoji = "🧼", label = "Hands", audioResId = R.raw.sound_hands),
    SoundItem(emoji = "🧸", label = "Toys", audioResId = R.raw.sound_toys),
    SoundItem(emoji = "❌", label = "Off", audioResId = R.raw.sound_off),
    SoundItem(emoji = "✞", label = "Please", audioResId = R.raw.sound_please),
    SoundItem(emoji = "🛏️", label = "Bedtime", audioResId = R.raw.sound_bedtime),
    SoundItem(emoji = "🤫", label = "Quiet",   audioResId = R.raw.sound_quiet)
)
