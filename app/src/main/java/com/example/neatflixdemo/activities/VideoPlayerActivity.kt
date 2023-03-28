package com.example.neatflixdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityVideoPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes

class VideoPlayerActivity : AppCompatActivity(){
    private lateinit var videoBinding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        videoBinding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(videoBinding.root)
        initializePlayer()
    }

    /**
     * this will initiate the video player in activity
     */
    private fun initializePlayer() {
        player = ExoPlayer.Builder(this) // <- context
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri(Constants.VIDEO_URL)
            .setMimeType(
                MimeTypes.APPLICATION_MP4)
            .build()
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this) // <- context
        )
            .createMediaSource(mediaItem)

        player!!.apply {
            setMediaSource(mediaSource)
            playWhenReady = true // start playing when the exoplayer has setup
            seekTo(0, 0L) // Start from the beginning
            prepare() // Change the state from idle.
        }.also {

            videoBinding.playerView.player = it
        }
    }
}