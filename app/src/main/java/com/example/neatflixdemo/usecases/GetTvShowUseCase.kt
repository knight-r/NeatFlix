package com.example.neatflixdemo.usecases

import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.repository.TvShowRepository

class GetTvShowUseCase(private val tvShowRepository: TvShowRepository) {
    suspend fun getTvShowGenres(): ResourceNotifier<GenreList?> {
        return tvShowRepository.getTvShowGenres()
    }
    suspend fun getPopularTvShows(): ResourceNotifier<PopularTvShows?> {
        return tvShowRepository.getPopularTvShows()
    }

    suspend fun getRecommendedTvShows(): ResourceNotifier<RecommendedTvShows??> {
        return tvShowRepository.getRecommendedTvShows()
    }

    suspend fun getTopRatedTvShows(): ResourceNotifier<TopRatedTvShows?> {
        return tvShowRepository.getTopRatedTvShows()
    }

    suspend fun getTvAiringToday(): ResourceNotifier<TvAiringToday?> {
        return tvShowRepository.getTvAiringToday()
    }


}