package com.example.neatflixdemo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityVideoPlayerBinding
import com.example.neatflixdemo.dataclasses.MovieObject
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoPlayerActivity : AppCompatActivity(){
    private lateinit var videoBinding: ActivityVideoPlayerBinding
    private var player: ExoPlayer? = null
    private val isPlaying get() = player?.isPlaying ?: false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        videoBinding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(videoBinding.root)
//        val bundle = intent.extras
//        val movieId = bundle?.getString("movie_id")
//        if (movieId != null) {
//            getVideoKey(movieId)
//        }
        initializePlayer()

    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this) // <- context
            .build()
        val mediaItem = MediaItem.Builder()
            .setUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")
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
 //   private fun getVideoKey(movieId:String){
//        val retrofitClient = RetrofitClient.getInstance()
//        val dataService = retrofitClient?.create(GetDataService::class.java)
//        dataService?.getMovieObject(movieId,Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
//            Callback<MovieObject?> {
//            override fun onResponse(call: Call<MovieObject?>, response: Response<MovieObject?>) {
//                val movieObject = response.body()
//
//
//            }
//
//            override fun onFailure(call: Call<MovieObject?>, t: Throwable) {
//               startActivity(Intent(this@VideoPlayerActivity, ErrorPageActivity::class.java))
//                t.message?.let { Log.e("MainActivity: ", it) }
//
//            }
//        })

   // }
}