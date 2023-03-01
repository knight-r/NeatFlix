package com.example.neatflixdemo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.RVAddViewAdapter

import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.adapter.RVGenreAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentFirstBinding
import com.example.neatflixdemo.databinding.RowAddItemBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirstFragment : Fragment() {
    private  var _binding:FragmentFirstBinding?= null
    private lateinit var llViewBinding: RowAddItemBinding
    private lateinit var layoutList: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFirstBinding.inflate(layoutInflater,container,false)
        llViewBinding = RowAddItemBinding.inflate(layoutInflater,container,false)
        layoutList = _binding!!.layoutList
        getMovieGenre()
        addView()
        return _binding?.root
    }

    /** this method get the list of genres name
     */
    private fun getMovieGenre(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)
        dataService?.getMovieGenres(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<GenreList?> {
            override fun onResponse(call: Call<GenreList?>, response: Response<GenreList?>) {
                val genreListBody = response.body()
                val genreList:List<Genre> = genreListBody?.genres ?: emptyList()

                setGenreListToRecyclerView(genreList)
            }

            override fun onFailure(call: Call<GenreList?>, t: Throwable) {
                t.message?.let { Log.e("MainActivity: ", it) }

            }
        })

    }
    /** this method set the list of genre to recyclerView
     */
    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        _binding?.rvMovieGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter(genreList,layoutList,"Movies")
        }
        _binding?.rvMovieGenreList?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun addView(){
        getPopularMovies()
        getNowPlayingMovies()
        getRecommendedMovies()
        //getLatestMovies()
        getTopRatedMovies()
        getUpComingMovies()

    }

    /** this method get the list of recommended movies
     */
    private fun getRecommendedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<Recommendations?> {
            override fun onResponse(call: Call<Recommendations?>, response: Response<Recommendations?>) {
                val listBody = response.body()
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Recommendations"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)

                layoutList.addView(llView)
            }
            override fun onFailure(call: Call<Recommendations?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of upcoming movies
     */
    private fun getUpComingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getUpComingMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<Upcoming?> {
            override fun onResponse(call: Call<Upcoming?>, response: Response<Upcoming?>) {
                val listBody = response.body()
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Upcoming Movies"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutList.addView(llView)
            }
            override fun onFailure(call: Call<Upcoming?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of top rated movies
     */
    private fun getTopRatedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TopRated?> {
            override fun onResponse(call: Call<TopRated?>, response: Response<TopRated?>) {
                val listBody = response.body()
                // Log.e("FirstFragment: ",response.body().toString())
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Top Rated"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutList.addView(llView)
            }
            override fun onFailure(call: Call<TopRated?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of now playing movies
     */
    private fun getNowPlayingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getNowPlayingMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<NowPlaying?> {
            override fun onResponse(call: Call<NowPlaying?>, response: Response<NowPlaying?>) {
                val listBody = response.body()
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Now Playing"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutList.addView(llView)
            }
            override fun onFailure(call: Call<NowPlaying?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    private fun getPopularMovies(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<PopularMovies?> {
            override fun onResponse(call: Call<PopularMovies?>, response: Response<PopularMovies?>) {
                val listBody = response.body()
                val popularMovieList: List<Result> = listBody!!.results
                val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
                val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
                textView.text = "Popular"
                val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
                setDataToRecyclerView(recyclerView, popularMovieList)
                layoutList.addView(llView)
            }
            override fun onFailure(call: Call<PopularMovies?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })

    }
    private fun setDataToRecyclerView(recyclerView:RecyclerView, popularMovieList:List<Result> ){
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


}