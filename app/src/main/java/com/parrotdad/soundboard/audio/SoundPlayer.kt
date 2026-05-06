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
     * Plays the audio resource identified by [resId].
     * Stops any currently playing sound before starting the new one.
     * Silently handles missing or unplayable resources.
     */
    fun play(context: Context, resId: Int) {
        stopAndRelease()
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
                }
                start()
            } ?: run {
                Log.w(TAG, "MediaPlayer.create returned null for resId=$resId – audio file may be missing")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to play audio resId=$resId", e)
            mediaPlayer = null
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
