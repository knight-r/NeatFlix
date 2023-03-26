package com.example.neatflixdemo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import coil.load
import com.example.neatflixdemo.R

import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Result

import com.example.neatflixdemo.databinding.ActivityShowDetailsBinding

class ShowDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityShowDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        val bundle = intent?.extras
        val resultData = bundle?.getSerializable(getString(R.string.result_data)) as Result
        binding.apply {
            ivMovieDetails.load(Constants.API_TMDB_IMAGE_BASE_URL + resultData.poster_path) {
                crossfade(true)
            }
            tvShowDetailsOverview.text = resultData.overview

            resultData?.name?.let {
                tvShowDetailsTitle.text = resultData?.name
            }
            resultData?.title?.let {
                tvShowDetailsTitle.text = resultData?.title
            }
            tvShowDetailsReleaseDate.text = resultData.release_date
            tvShowDetailsRating.text = resultData.vote_average.toString()
        }
        binding.ivYoutubeVideo.setOnClickListener{
            val intent = Intent(this, VideoPlayerActivity::class.java)
            startActivity(intent)

        }

    }
    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals(getString(R.string.signup_activity))) {
            checkLoginStatus()
        }
    }
}