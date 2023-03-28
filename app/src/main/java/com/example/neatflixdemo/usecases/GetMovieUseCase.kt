package com.example.neatflixdemo.usecases

import com.example.neatflixdemo.repository.MovieRepository


class GetMovieUseCase(private val movieRepository: MovieRepository) {
 suspend fun getMovies() {

 }
}