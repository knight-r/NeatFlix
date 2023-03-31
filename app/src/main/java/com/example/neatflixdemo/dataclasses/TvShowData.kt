package com.example.neatflixdemo.dataclasses

data class TvShowData(
    var popularTvShows: List<Result>,
    var recommendedTvShows:  List<Result>,
    var topRatedTvShows:  List<Result>,
    var tvAiringToday: List<Result>,
    var totalTvShows: MutableList<Result>,
    var genres: List<Genre>
)
