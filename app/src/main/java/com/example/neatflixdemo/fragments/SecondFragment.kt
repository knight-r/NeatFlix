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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSecondBinding.inflate(layoutInflater,container,false)
        layoutListTv = _binding!!.layoutListTv
        getTvGenreList()
        addView()
        return _binding?.root

    }

    private fun addView() {
        getPopularTvShows()
        getTopRatedTvShows()
        getTvAiringToday()
        getRecommendedTvShows()

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
                // Log.e("FirstFragment: ",response.body().toString())
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Top Rated"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutListTv.addView(llView)
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
                // Log.e("FirstFragment: ",response.body().toString())
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView: TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Popular"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutListTv.addView(llView)
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
                // Log.e("FirstFragment: ",response.body().toString())
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView: TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Airing Today"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutListTv.addView(llView)
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
                // Log.e("FirstFragment: ",response.body().toString())
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Recommendations"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutListTv.addView(llView)
            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })
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
    interface SecondFragmentToActivity{
        fun sendTvShowData(totalMovieList:List<Result>)
    }

}