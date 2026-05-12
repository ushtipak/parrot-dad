package com.parrotdad.soundboard.data

import com.parrotdad.soundboard.R

/**
 * Static list of 9 soundboard items.
 * Audio files are expected in res/raw/.
 */
val soundboardItems: List<SoundItem> = listOf(
    SoundItem(key = "teeth", emoji = "🪥", label = "Teeth", audioResId = R.raw.sound_teeth),
    SoundItem(key = "towel", emoji = "🧻", label = "Towel", audioResId = R.raw.sound_towel),
    SoundItem(key = "shoes", emoji = "👟", label = "Shoes", audioResId = R.raw.sound_shoes),
    SoundItem(key = "hands", emoji = "🧼", label = "Hands", audioResId = R.raw.sound_hands),
    SoundItem(key = "toys", emoji = "🧸", label = "Toys", audioResId = R.raw.sound_toys),
    SoundItem(key = "off", emoji = "❌", label = "Off", audioResId = R.raw.sound_off),
    SoundItem(key = "please", emoji = "🙏", label = "Please", audioResId = R.raw.sound_please),
    SoundItem(key = "bedtime", emoji = "🛏️", label = "Bedtime", audioResId = R.raw.sound_bedtime),
    SoundItem(key = "quiet", emoji = "🤫", label = "Quiet", audioResId = R.raw.sound_quiet)
)
