package com.example.neatflixdemo.repository

import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.services.GetDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository(private val dataService: GetDataService?) {

    suspend fun getMovieGenres(): ResourceNotifier<GenreList?> = withContext(Dispatchers.IO){
        try {
            val response = dataService?.getMovieGenres(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)
            if (response!!.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }

   suspend fun getPopularMovies(): ResourceNotifier<PopularMovies?> = withContext(Dispatchers.IO){
       try {
           val response = dataService?.getPopularMovies(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

           if (response!!.isSuccessful) {
               ResourceNotifier.Success(response.body())
           } else {
               ResourceNotifier.Error("Error: $response")
           }
       } catch (e: Exception) {
           ResourceNotifier.Error("Error: ${e.localizedMessage}")
       }
   }
    suspend fun getRecommendedMovies(): ResourceNotifier<RecommendedMovies?> = withContext(Dispatchers.IO){
        try {
            val response = dataService?.getRecommendedMovies(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response!!.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getTopRatedMovies(): ResourceNotifier<TopRatedMovies?> = withContext(Dispatchers.IO){
        try {
            val response = dataService?.getTopRatedMovies(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response!!.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getUpcomingMovies(): ResourceNotifier<UpcomingMovies?> = withContext(Dispatchers.IO){
        try {
            val response = dataService?.getUpComingMovies(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response!!.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getNowPlayingMovies(): ResourceNotifier<NowPlayingMovies?> = withContext(Dispatchers.IO){
        try {
            val response = dataService?.getNowPlayingMovies(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response!!.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
}