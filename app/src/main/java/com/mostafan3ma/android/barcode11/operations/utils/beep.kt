package com.mostafan3ma.android.barcode11.operations.utils

import android.media.MediaPlayer
import com.mostafan3ma.android.barcode11.R
import android.content.Context

class BeepPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    init {
// Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(context, R.raw.peep)
    }

    fun play() {
// Check if MediaPlayer is null or not playing
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.peep)
        }
        // Start playing the sound
        mediaPlayer?.start()
    }

    fun release() { // Release MediaPlayer resources
        mediaPlayer?.release()
        mediaPlayer = null
    }
}