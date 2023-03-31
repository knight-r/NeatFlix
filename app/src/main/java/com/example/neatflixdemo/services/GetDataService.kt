package com.example.neatflixdemo.services

import com.example.neatflixdemo.dataclasses.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GetDataService {

    @GET("3/genre/movie/list?")
    suspend fun getMovieGenres(@Query("api_key") api_key:String,
                       @Query("language") language:String):Response<GenreList>

    @GET("3/genre/tv/list?")
    suspend fun getTvShowGenres(@Query("api_key") api_key:String,
                    @Query("language") language:String):Response<GenreList>

    @GET("3/movie/popular?")
    suspend fun getPopularMovies(@Query("api_key") api_key:String,
                         @Query("language") language:String):Response<PopularMovies>

    @GET("3/movie/315162/recommendations?")
    suspend fun getRecommendedMovies(@Query("api_key") api_key:String,
                         @Query("language") language:String):Response<RecommendedMovies>

    @GET("3/movie/latest?")
    suspend fun getLatestMovies(@Query("api_key") api_key:String,
                        @Query("language") language:String):Call<PopularMovies>

    @GET("3/movie/now_playing?")
    suspend fun getNowPlayingMovies(@Query("api_key") api_key:String,
                            @Query("language") language:String):Response<NowPlayingMovies>

    @GET("3/movie/top_rated?")
    suspend fun getTopRatedMovies(@Query("api_key") api_key:String,
                          @Query("language") language:String):Response<TopRatedMovies>

    @GET("3/movie/upcoming?")
    suspend fun getUpComingMovies(@Query("api_key") api_key:String,
                          @Query("language") language:String):Response<UpcomingMovies>

    @GET("3/tv/popular?")
    suspend fun getPopularTvShows(@Query("api_key") api_key:String,
                         @Query("language") language:String):Response<PopularTvShows>

    @GET("3/tv/top_rated?")
    suspend fun getTopRatedTvShows(@Query("api_key") api_key:String,
                          @Query("language") language:String):Response<TopRatedTvShows>
    @GET("3/tv/airing_today?")
    suspend fun getTvAiringToday(@Query("api_key") api_key:String,
                           @Query("language") language:String):Response<TvAiringToday>
    @GET("3/tv/airing_today?")
    suspend fun getRecommendedTvShows(@Query("api_key") api_key:String,
                         @Query("language") language:String):Response<RecommendedTvShows>

    @GET("3/movie/{movie_id}/videos?")
    suspend fun getMovieObject(
        @Path("movie_id") movieId: String,
        @Query("api_key") api_key: String,
        @Query("language") language:String
    ): Call<MovieObject>
}