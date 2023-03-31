package com.example.neatflixdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.repository.MovieRepository
import com.example.neatflixdemo.usecases.GetMovieUseCase
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository): ViewModel() {
    val popularMovies : MutableLiveData<ResourceNotifier<PopularMovies?>> = MutableLiveData()
    val recommendedMovies: MutableLiveData<ResourceNotifier<RecommendedMovies?>> = MutableLiveData()
    val topRatedMovies: MutableLiveData<ResourceNotifier<TopRatedMovies?>> = MutableLiveData()
    val nowPlayingMovies: MutableLiveData<ResourceNotifier<NowPlayingMovies?>> = MutableLiveData()
    val upcomingMovies: MutableLiveData<ResourceNotifier<UpcomingMovies?>> = MutableLiveData()
    val movieGenres: MutableLiveData<ResourceNotifier<GenreList?>> = MutableLiveData()

    fun getMovieGenres(){
        viewModelScope.launch {
            movieGenres.postValue(ResourceNotifier.Loading())
            movieGenres.postValue(GetMovieUseCase(movieRepository).getMovieGenres())
        }
    }
    fun getPopularMovies(){
        viewModelScope.launch {
            popularMovies.postValue(ResourceNotifier.Loading())
            popularMovies.postValue(GetMovieUseCase(movieRepository).getPopularMovies())
        }
    }

    fun getRecommendedMovies(){
        viewModelScope.launch {
            recommendedMovies.postValue(ResourceNotifier.Loading())
            recommendedMovies.postValue(GetMovieUseCase(movieRepository).getRecommendedMovies())
        }
    }

    fun getTopRatedMovies(){
        viewModelScope.launch {
            topRatedMovies.postValue(ResourceNotifier.Loading())
            topRatedMovies.postValue(GetMovieUseCase(movieRepository).getTopRateMovies())
        }
    }

    fun getUpcomingMovies(){
        viewModelScope.launch {
            upcomingMovies.postValue(ResourceNotifier.Loading())
            upcomingMovies.postValue(GetMovieUseCase(movieRepository).getUpcomingMovies())
        }
    }

    fun getNowPlayingMovies(){
        viewModelScope.launch {
            nowPlayingMovies.postValue(ResourceNotifier.Loading())
            nowPlayingMovies.postValue(GetMovieUseCase(movieRepository).getNowPlayingMovies())
        }
    }




}