package com.example.neatflixdemo.services

import com.example.neatflixdemo.dataclasses.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetDataService {

    @GET("3/genre/movie/list?")
    fun getMovieGenres(@Query("api_key") api_key:String,
                       @Query("language") language:String):Call<GenreList>

    @GET("3/genre/tv/list?")
    fun getTvGenres(@Query("api_key") api_key:String,
                    @Query("language") language:String):Call<GenreList>
   // https://api.themoviedb.org/3/genre/movie/list?api_key=0733e6cf2426a163f8deedac00044740&language=en-US


    @GET("3/movie/popular?")
    fun getPopularMovies(@Query("api_key") api_key:String,
                         @Query("language") language:String):Call<PopularMovies>

    @GET("3/movie/315162/recommendations?")
    fun getRecommendedMovies(@Query("api_key") api_key:String,
                         @Query("language") language:String):Call<Recommendations>

    @GET("3/movie/latest?")
    fun getLatestMovies(@Query("api_key") api_key:String,
                        @Query("language") language:String):Call<PopularMovies>

    @GET("3/movie/now_playing?")
    fun getNowPlayingMovies(@Query("api_key") api_key:String,
                            @Query("language") language:String):Call<NowPlaying>

    @GET("3/movie/top_rated?")
    fun getTopRatedMovies(@Query("api_key") api_key:String,
                          @Query("language") language:String):Call<TopRated>

    @GET("3/movie/upcoming?")
    fun getUpComingMovies(@Query("api_key") api_key:String,
                          @Query("language") language:String):Call<Upcoming>

    @GET("3/tv/popular?")
    fun getPopularTvShows(@Query("api_key") api_key:String,
                         @Query("language") language:String):Call<PopularTvShows>

    @GET("3/tv/top_rated?")
    fun getTopRatedTvShows(@Query("api_key") api_key:String,
                          @Query("language") language:String):Call<TopRatedTvShows>
    @GET("3/tv/airing_today?")
    fun getTvAiringToday(@Query("api_key") api_key:String,
                           @Query("language") language:String):Call<TvAiringToday>
    @GET("3/tv/airing_today?")
    fun getRecommendedTvShows(@Query("api_key") api_key:String,
                         @Query("language") language:String):Call<RecommendedTvShows>

    @GET("3/movie/{movie_id}/videos?")
    fun getMovieObject(
        @Path("movie_id") movieId: String,
        @Query("api_key") api_key: String,
        @Query("language") language:String
    ): Call<MovieObject>






//https://api.themoviedb.org/3/movie/popular?api_key=0733e6cf2426a163f8deedac00044740&language=en-US&page=1

}