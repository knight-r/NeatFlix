package com.example.neatflixdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import coil.load
import com.example.neatflixdemo.databinding.ActivityShowDetailsBinding

@Suppress("DEPRECATION")
class ShowDetailsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityShowDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle: Bundle? = intent?.extras
        val posterPath = bundle?.getString("result_image")
        val overView = bundle?.getSerializable("result_overview")

        binding.ivMovieDetails.load("https://image.tmdb.org/t/p/original" + posterPath.toString() ) {
            crossfade(true)
        }
        binding.tvMovieDetails.text = "Overview: " + overView.toString()
    }
}