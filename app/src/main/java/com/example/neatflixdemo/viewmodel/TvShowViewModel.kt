package com.example.neatflixdemo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.repository.TvShowRepository
import com.example.neatflixdemo.usecases.GetMovieUseCase
import com.example.neatflixdemo.usecases.GetTvShowUseCase
import kotlinx.coroutines.launch

class TvShowViewModel(private val tvShowRepository: TvShowRepository): ViewModel() {

    val popularTvShows : MutableLiveData<ResourceNotifier<PopularTvShows?>> = MutableLiveData()
    val recommendedtvShows: MutableLiveData<ResourceNotifier<RecommendedTvShows?>> = MutableLiveData()
    val topRatedTvShows: MutableLiveData<ResourceNotifier<TopRatedTvShows?>> = MutableLiveData()
    val tvAiringToday: MutableLiveData<ResourceNotifier<TvAiringToday?>> = MutableLiveData()
    val tvShowGenres: MutableLiveData<ResourceNotifier<GenreList?>> = MutableLiveData()

    fun getTvShowGenres(){
        viewModelScope.launch {
            tvShowGenres.postValue(ResourceNotifier.Loading())
            tvShowGenres.postValue(GetTvShowUseCase(tvShowRepository).getTvShowGenres())
        }
    }

    fun getPopularTvShows(){
        viewModelScope.launch {
            popularTvShows.postValue(ResourceNotifier.Loading())
            popularTvShows.postValue(GetTvShowUseCase(tvShowRepository).getPopularTvShows())
        }
    }

    fun getRecommendedTvShows(){
        viewModelScope.launch {
            recommendedtvShows.postValue(ResourceNotifier.Loading())
            recommendedtvShows.postValue(GetTvShowUseCase(tvShowRepository).getRecommendedTvShows())
        }
    }

    fun getTopRatedTvShows(){
        viewModelScope.launch {
            topRatedTvShows.postValue(ResourceNotifier.Loading())
            topRatedTvShows.postValue(GetTvShowUseCase(tvShowRepository).getTopRatedTvShows())
        }
    }

    fun getTvAiringToday(){
        viewModelScope.launch {
            tvAiringToday.postValue(ResourceNotifier.Loading())
            tvAiringToday.postValue(GetTvShowUseCase(tvShowRepository).getTvAiringToday())
        }
    }
}