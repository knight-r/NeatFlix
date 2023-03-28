package com.example.neatflixdemo.dataclasses

data class UpcomingMovies(
    val dates: Dates,
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)