package com.example.neatflixdemo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neatflixdemo.dataclasses.Result
import com.example.neatflixdemo.repository.MovieRepository

class MainViewModel(private val repository: MovieRepository): ViewModel() {
    var data = MutableLiveData<String>()
    private val resultData : LiveData<String>
    get() = data
    fun setData(str:String){
        data.value = str
    }
    fun getData(): String? {
        return resultData.value
    }
}