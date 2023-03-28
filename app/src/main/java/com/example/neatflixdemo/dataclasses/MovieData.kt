package com.example.neatflixdemo.dataclasses

data class MovieData(
    var popularMovies: List<Result>,
    var recommendedMovies: List<Result>,
    var topRatedMovies: List<Result>,
    var nowPlayingMovies: List<Result>,
    var upcomingMovies: List<Result>,
    var totalMovies : MutableList<Result>
)

