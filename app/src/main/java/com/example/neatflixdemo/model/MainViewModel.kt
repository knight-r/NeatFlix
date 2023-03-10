package com.example.neatflixdemo.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.neatflixdemo.dataclasses.Result

class MainViewModel: ViewModel() {
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