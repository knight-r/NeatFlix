package com.example.neatflixdemo.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.activities.MainActivity
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.RVAddViewAdapter
import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.adapter.RVGenreAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentSecondBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondFragment : Fragment() {
    private var _binding:FragmentSecondBinding?=null
    private lateinit var layoutListTv: LinearLayout
    private var popularTvShowList:List<Result> = emptyList()
    private var recommendedTvShowList:List<Result> = emptyList()
    private var topRatedTvShowList:List<Result> = emptyList()
    private var tvAiringTodayList:List<Result> = emptyList()
    private var totalTvShowList = mutableListOf<Result>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSecondBinding.inflate(layoutInflater,container,false)
        layoutListTv = _binding!!.layoutListTv
        getTvGenreList()
        addView()
        (activity as MainActivity?)?.sendTvShowData(totalTvShowList)
        return _binding?.root

    }

    private fun addView() {
        if(popularTvShowList.isNotEmpty()) {
            addViewToLayoutList("Popular",popularTvShowList)
        } else {
            getPopularTvShows()
        }
        if(recommendedTvShowList.isNotEmpty()) {
            addViewToLayoutList("Recommendations",recommendedTvShowList)
        } else {
            getRecommendedTvShows()
        }
        if(topRatedTvShowList.isNotEmpty()) {
            addViewToLayoutList("Top Rated",topRatedTvShowList)
        }else {
            getTopRatedTvShows()
        }
        if(tvAiringTodayList.isNotEmpty()){
            addViewToLayoutList("Tv Airing Today", tvAiringTodayList)
        }else {
            getTvAiringToday()
        }

    }
    /** this method get the list of TvShows genres
     */
    private fun getTvGenreList(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)
        dataService?.getTvGenres("0733e6cf2426a163f8deedac00044740","en-US")?.enqueue(object:
            Callback<GenreList?> {
            override fun onResponse(call: Call<GenreList?>, response: Response<GenreList?>) {
                val genreListBody = response.body()
                //Log.e("MainActivity: ",response.body().toString())
                val genreList:List<Genre> = genreListBody?.genres ?: emptyList()
                setGenreListToRecyclerView(genreList)

            }
            override fun onFailure(call: Call<GenreList?>, t: Throwable) {
                t.message?.let { Log.e("MainActivity: ", it) }
            }
        })

    }


    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        _binding?.rvTvGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter(genreList,layoutListTv,"TvShows")
        }
        _binding?.rvTvGenreList?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    /** this method get the list of top rated TvShows
     */
    private fun getTopRatedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TopRatedTvShows?> {
            override fun onResponse(call: Call<TopRatedTvShows?>, response: Response<TopRatedTvShows?>) {
                val listBody = response.body()
                topRatedTvShowList = listBody!!.results
                addViewToLayoutList("Top Rated",topRatedTvShowList)
                addTotalTvShowList(topRatedTvShowList)
            }
            override fun onFailure(call: Call<TopRatedTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of popular TvShows
     */
    private fun getPopularTvShows(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<PopularTvShows?> {
            override fun onResponse(call: Call<PopularTvShows?>, response: Response<PopularTvShows?>) {
                val listBody = response.body()
                popularTvShowList = listBody!!.results
                addViewToLayoutList("Popular",popularTvShowList)
                addTotalTvShowList(popularTvShowList)
            }
            override fun onFailure(call: Call<PopularTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })

    }

    /** this method get the list of TvShows Airing today
     */
    private fun getTvAiringToday(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTvAiringToday(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TvAiringToday?> {
            override fun onResponse(call: Call<TvAiringToday?>, response: Response<TvAiringToday?>) {
                val listBody = response.body()
                tvAiringTodayList = listBody!!.results
                addViewToLayoutList("Tv Airing Today",tvAiringTodayList)
                addTotalTvShowList(tvAiringTodayList)
            }
            override fun onFailure(call: Call<TvAiringToday?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })

    }

    /** this method get the list of recommended TvShows
     */
    private fun getRecommendedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<RecommendedTvShows?> {
            override fun onResponse(call: Call<RecommendedTvShows?>, response: Response<RecommendedTvShows?>) {
                val listBody = response.body()
                recommendedTvShowList = listBody!!.results
                addViewToLayoutList("Recommendations",recommendedTvShowList)
                addTotalTvShowList(recommendedTvShowList)
            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })
    }
    /**
     *  this method add view item in layout list
     */
    fun addViewToLayoutList(textString:String, movieList:List<Result>){
        val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
        val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
        textView.text = textString
        val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
        setDataToRecyclerView(recyclerView, movieList)
        layoutListTv.addView(llView)
    }

    /** this method sets the list of Tv Shows of particular category to recyclerView
     */
    private fun setDataToRecyclerView(recyclerView: RecyclerView, popularMovieList:List<Result> ){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVAddViewAdapter(popularMovieList)
        }
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }
    fun addTotalTvShowList(tvList:List<Result>){
        for(items in tvList){
            totalTvShowList.add(items)
        }
    }
    interface SecondFragmentToActivity{
        fun sendTvShowData(totalTvShowList:List<Result>)
    }


}