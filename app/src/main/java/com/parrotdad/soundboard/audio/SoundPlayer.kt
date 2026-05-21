package com.parrotdad.soundboard.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

private const val TAG = "SoundPlayer"

/**
 * Manages MediaPlayer lifecycle for the soundboard.
 * Only one sound plays at a time; each new play() call stops and releases the previous player.
 */
class SoundPlayer {

    private var mediaPlayer: MediaPlayer? = null

    /**
     * Plays the audio resource identified by [resId], repeating [repeat] times total.
     * Stops any currently playing sound before starting the new one.
     * Silently handles missing or unplayable resources.
     */
    fun play(context: Context, resId: Int, repeat: Int = 1) {
        stopAndRelease()
        var remaining = repeat.coerceAtLeast(1)
        fun startNext() {
            if (remaining <= 0) return
            remaining--
            try {
                mediaPlayer = MediaPlayer.create(context, resId)?.apply {
                    setOnErrorListener { mp, what, extra ->
                        Log.w(TAG, "MediaPlayer error: what=$what extra=$extra")
                        mp.release()
                        mediaPlayer = null
                        true
                    }
                    setOnCompletionListener { mp ->
                        mp.release()
                        mediaPlayer = null
                        if (remaining > 0) startNext()
                    }
                    start()
                } ?: run {
                    Log.w(TAG, "MediaPlayer.create returned null for resId=$resId")
                    null
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to play audio resId=$resId", e)
                mediaPlayer = null
            }
        }
        startNext()
    }

    /**
     * Plays audio from an absolute [filePath] (used for custom user recordings),
     * repeating [repeat] times total.
     * Falls back to [fallbackResId] if the file doesn't exist or fails to play.
     */
    fun playCustomOrFallback(context: Context, filePath: String?, fallbackResId: Int, repeat: Int = 1) {
        if (filePath != null && java.io.File(filePath).exists()) {
            stopAndRelease()
            var remaining = repeat.coerceAtLeast(1)
            fun startNext() {
                if (remaining <= 0) return
                remaining--
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(filePath)
                        setOnErrorListener { mp, what, extra ->
                            Log.w(TAG, "MediaPlayer error on file: what=$what extra=$extra")
                            mp.release()
                            mediaPlayer = null
                            true
                        }
                        setOnCompletionListener { mp ->
                            mp.release()
                            mediaPlayer = null
                            if (remaining > 0) startNext()
                        }
                        prepare()
                        start()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to play file $filePath, falling back to resource", e)
                    mediaPlayer = null
                    play(context, fallbackResId, remaining + 1)
                }
            }
            startNext()
        } else {
            play(context, fallbackResId, repeat)
        }
    }

    /**
     * Stops playback and releases the MediaPlayer.
     * Safe to call even when nothing is playing.
     */
    fun stopAndRelease() {
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) mp.stop()
                mp.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing MediaPlayer", e)
            }
        }
        mediaPlayer = null
    }
}
