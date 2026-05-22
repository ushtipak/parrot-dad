package com.parrotdad.soundboard.data

import com.parrotdad.soundboard.R

/**
 * Static list of 9 soundboard items.
 * Audio files are expected in res/raw/.
 */
val soundboardItems: List<SoundItem> = listOf(
    SoundItem(key = "teeth",   emoji = "🪥",  audioResId = R.raw.sound_teeth),
    SoundItem(key = "towel",   emoji = "🧻",  audioResId = R.raw.sound_towel),
    SoundItem(key = "shoes",   emoji = "👟",  audioResId = R.raw.sound_shoes),
    SoundItem(key = "hands",   emoji = "🧼",  audioResId = R.raw.sound_hands),
    SoundItem(key = "toys",    emoji = "🧸",  audioResId = R.raw.sound_toys),
    SoundItem(key = "off",     emoji = "❌",  audioResId = R.raw.sound_off),
    SoundItem(key = "please",  emoji = "🙏",  audioResId = R.raw.sound_please),
    SoundItem(key = "bedtime", emoji = "🛏️", audioResId = R.raw.sound_bedtime),
    SoundItem(key = "quiet",   emoji = "🤫",  audioResId = R.raw.sound_quiet)
)

val violinItem = SoundItem(key = "violin", emoji = "🎻", audioResId = R.raw.violin)
