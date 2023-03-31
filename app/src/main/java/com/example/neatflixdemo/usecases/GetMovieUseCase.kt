package com.example.neatflixdemo.usecases

import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.repository.MovieRepository

class GetMovieUseCase(private val movieRepository: MovieRepository) {

 suspend fun getMovieGenres(): ResourceNotifier<GenreList?> {
  return movieRepository.getMovieGenres()
 }

 suspend fun getPopularMovies(): ResourceNotifier<PopularMovies?> {
    return movieRepository.getPopularMovies()
 }

 suspend fun getRecommendedMovies(): ResourceNotifier<RecommendedMovies??> {
  return movieRepository.getRecommendedMovies()
 }

 suspend fun getTopRateMovies(): ResourceNotifier<TopRatedMovies?> {
  return movieRepository.getTopRatedMovies()
 }

 suspend fun getUpcomingMovies(): ResourceNotifier<UpcomingMovies?> {
  return movieRepository.getUpcomingMovies()
 }

 suspend fun getNowPlayingMovies(): ResourceNotifier<NowPlayingMovies?> {
  return movieRepository.getNowPlayingMovies()
 }


}