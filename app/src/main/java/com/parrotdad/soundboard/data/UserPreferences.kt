package com.parrotdad.soundboard.data

import android.content.Context
import androidx.core.content.edit

private const val PREFS_NAME = "soundboard_prefs"
private const val PREFIX = "custom_path_"

/**
 * Lightweight SharedPreferences wrapper that persists custom recording paths.
 * Key = SoundItem.key (e.g. "teeth"), value = absolute file path or null for default.
 */
object UserPreferences {

    fun getCustomPath(context: Context, key: String): String? =
        prefs(context).getString(PREFIX + key, null)

    fun setCustomPath(context: Context, key: String, path: String) =
        prefs(context).edit { putString(PREFIX + key, path) }

    fun clearCustomPath(context: Context, key: String) =
        prefs(context).edit { remove(PREFIX + key) }

    fun hasCustomSound(context: Context, key: String): Boolean =
        getCustomPath(context, key) != null

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
