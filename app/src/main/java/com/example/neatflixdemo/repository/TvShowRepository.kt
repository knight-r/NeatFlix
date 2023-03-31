package com.example.neatflixdemo.repository

import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.services.GetDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TvShowRepository(private val dataService: GetDataService) {

    suspend fun getTvShowGenres(): ResourceNotifier<GenreList?> = withContext(Dispatchers.IO){
        try {
            val response = dataService.getTvShowGenres(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getPopularTvShows(): ResourceNotifier<PopularTvShows?> = withContext(Dispatchers.IO){
        try {
            val response = dataService.getPopularTvShows(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getRecommendedTvShows(): ResourceNotifier<RecommendedTvShows?> = withContext(
        Dispatchers.IO){
        try {
            val response = dataService.getRecommendedTvShows(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getTopRatedTvShows(): ResourceNotifier<TopRatedTvShows?> = withContext(Dispatchers.IO){
        try {
            val response = dataService.getTopRatedTvShows(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
    suspend fun getTvAiringToday(): ResourceNotifier<TvAiringToday?> = withContext(Dispatchers.IO){
        try {
            val response = dataService.getTvAiringToday(Constants.API_KEY_TMDB, Constants.API_LANGUAGE)

            if (response.isSuccessful) {
                ResourceNotifier.Success(response.body())
            } else {
                ResourceNotifier.Error("Error: $response")
            }
        } catch (e: Exception) {
            ResourceNotifier.Error("Error: ${e.localizedMessage}")
        }
    }
}