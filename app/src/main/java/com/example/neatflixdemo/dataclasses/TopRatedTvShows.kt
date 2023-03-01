package com.example.neatflixdemo.dataclasses

data class TopRatedTvShows(
    val page: Int,
    val results: List<Result>,
    val total_pages: Int,
    val total_results: Int
)