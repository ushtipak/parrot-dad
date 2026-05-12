package com.parrotdad.soundboard.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

private const val TAG = "RecordingManager"

/**
 * Manages a single MediaRecorder session.
 * Recordings are saved to the app's private files directory as M4A files.
 */
class RecordingManager {

    private var recorder: MediaRecorder? = null
    private var currentOutputPath: String? = null

    /** True while a recording is actively in progress. */
    val isRecording: Boolean get() = recorder != null

    /**
     * Starts recording into a temporary file and returns the output path.
     * Stops any in-progress recording first.
     */
    fun startRecording(context: Context, key: String): String? {
        stopRecording()
        val file = tempFileFor(context, key)
        currentOutputPath = file.absolutePath
        return try {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            recorder?.release()
            recorder = null
            currentOutputPath = null
            null
        }
    }

    /**
     * Stops the current recording and returns the path to the saved file,
     * or null if nothing was being recorded.
     */
    fun stopRecording(): String? {
        val path = currentOutputPath
        return try {
            recorder?.apply {
                stop()
                release()
            }
            path
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recorder", e)
            null
        } finally {
            recorder = null
            currentOutputPath = null
        }
    }

    /** Releases resources without saving. */
    fun cancel() {
        try {
            recorder?.release()
        } catch (_: Exception) {}
        recorder = null
        currentOutputPath = null
    }

    companion object {
        /** Returns the permanent saved path for a custom recording for [key]. */
        fun savedFileFor(context: Context, key: String): File =
            File(context.filesDir, "custom_$key.m4a")

        /** Returns a temp path used during recording (before confirming save). */
        fun tempFileFor(context: Context, key: String): File =
            File(context.cacheDir, "recording_$key.m4a")

        /** Promotes the temp recording to the permanent saved file. */
        fun commitRecording(context: Context, key: String): String? {
            val temp = tempFileFor(context, key)
            val saved = savedFileFor(context, key)
            return if (temp.exists()) {
                temp.copyTo(saved, overwrite = true)
                temp.delete()
                saved.absolutePath
            } else null
        }

        /** Deletes the saved custom recording for [key]. */
        fun deleteCustomRecording(context: Context, key: String) {
            savedFileFor(context, key).delete()
            tempFileFor(context, key).delete()
        }
    }
}
