package com.example.neatflixdemo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.SearchAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivitySearchBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SearchActivity : AppCompatActivity() {
    private lateinit var searchBinding : ActivitySearchBinding
    private lateinit var totalMovieList: List<Result>
    private var totalTvShowList = mutableListOf<Result>()
    private var totalList = mutableListOf<Result>()
    private lateinit var searchRV: RecyclerView
    private lateinit var listAdapter: SearchAdapter

    private lateinit var searchView: SearchView
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)
        searchRV = searchBinding.rvSearch
        searchView = searchBinding.searchView
        searchView.isIconified = false

        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        actionBar?.hide()

        val bundle: Bundle? = intent.extras
        totalMovieList = bundle?.getSerializable("movie_list") as List<Result>
        totalTvShowList = (bundle?.getSerializable("tv_show_list") as List<Result>).toMutableList()

        if(totalTvShowList.isEmpty()){
            getTotalTvShowList()
        }
        totalList = totalMovieList as MutableList<Result>
        for(items in totalTvShowList){
            totalList.add(items)
        }

        buildRecyclerView()

        searchView.queryHint = Html.fromHtml("<font color = #C7B9B9>" + "Search movies and Tv Shows" + "</font>")

        val id = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = searchView.findViewById<View>(id) as TextView
        textView.setTextColor(Color.WHITE)

        queryOnInput()
        searchBinding.backButton.setOnClickListener{
            finish()
        }


    }
    private fun queryOnInput(){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filter(newText)
                return false
            }
        })
    }
    private fun filter(text: String) {
        var filteredList = mutableListOf<Result>()
        for (item in totalList) {
            item?.title?.let {
                if (item.title?.toLowerCase()!!.contains(text.lowercase(Locale.getDefault()))) {
                    filteredList.add(item)
                }
            }
            item?.name?.let {
                if (item.name?.toLowerCase()!!.contains(text.lowercase(Locale.getDefault()))) {
                    filteredList.add(item)
                }
            }
        }

        listAdapter.filterList(filteredList)
    }

    private fun buildRecyclerView() {
        listAdapter = SearchAdapter(totalList )
        val manager = LinearLayoutManager(this)
        searchRV.apply{
            setHasFixedSize(true)
            layoutManager = manager
            adapter = listAdapter

        }
    }
    private fun getTotalTvShowList(){
        getTopRatedTvShows()
        getPopularTvShows()
        getTvAiringToday()
        getRecommendedTvShows()
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
                val tvList :List<Result> = listBody!!.results
                addTotalTvShowList(tvList)
            }
            override fun onFailure(call: Call<TopRatedTvShows?>, t: Throwable) {
                startActivity(Intent(this@SearchActivity, ErrorPageActivity::class.java))
                Log.e("SearchActivity: ",t.message.toString())
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
                val tvList :List<Result> = listBody!!.results
                addTotalTvShowList(tvList)
            }
            override fun onFailure(call: Call<PopularTvShows?>, t: Throwable) {
                startActivity(Intent(this@SearchActivity, ErrorPageActivity::class.java))
                Log.e("SearchActivity ",t.message.toString())
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
                val tvList :List<Result> = listBody!!.results
                addTotalTvShowList(tvList)
            }
            override fun onFailure(call: Call<TvAiringToday?>, t: Throwable) {
                startActivity(Intent(this@SearchActivity, ErrorPageActivity::class.java))
                Log.e("SearchActivity ",t.message.toString())
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
                val tvList :List<Result> = listBody!!.results
                addTotalTvShowList(tvList)
            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                startActivity(Intent(this@SearchActivity, ErrorPageActivity::class.java))
                Log.e("SearchActivity ",t.message.toString())
            }
        })
    }
    fun addTotalTvShowList(tvList:List<Result>){
        for(items in tvList){
            totalTvShowList.add(items)
        }
    }
}


