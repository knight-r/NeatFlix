package com.example.neatflixdemo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.activities.DashboardActivity
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ErrorPageActivity
import com.example.neatflixdemo.activities.ShowCategory
import com.example.neatflixdemo.adapter.RVAddViewAdapter
import com.example.neatflixdemo.adapter.RVGenreAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentMoviesBinding
import com.example.neatflixdemo.databinding.RowAddItemBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class MovieFragment : Fragment() {
    private  var fragmentMoviesBinding:FragmentMoviesBinding?= null
    private lateinit var llViewBinding: RowAddItemBinding
    private lateinit var layoutList: LinearLayout
    private var popularMovieList:List<Result> = emptyList()
    private var nowPlayingList:List<Result> = emptyList()
    private var recommendedMovieList:List<Result> = emptyList()
    private var topRatedMovieList:List<Result> = emptyList()
    private var upcomingMovieList:List<Result> = emptyList()
    private var totalMovieList = mutableListOf<Result>()
    private var hashMap = mutableMapOf<String,Int>()
    private val firstTabName:String = "Movies"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentMoviesBinding = FragmentMoviesBinding.inflate(layoutInflater,container,false)
        llViewBinding = RowAddItemBinding.inflate(layoutInflater,container,false)
        layoutList = fragmentMoviesBinding!!.layoutList

        getMovieGenre()
        addView()
        (activity as DashboardActivity?)?.onReceivedData(totalMovieList)
        fragmentMoviesBinding!!.refreshLayout.setOnRefreshListener {
            addView()
            fragmentMoviesBinding!!.refreshLayout.isRefreshing = false
        }
        return fragmentMoviesBinding?.root
    }

    /** this method get the list of genres name
     */
    private fun getMovieGenre(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)
        dataService?.getMovieGenres(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<GenreList?> {
            override fun onResponse(call: Call<GenreList?>, response: Response<GenreList?>) {
                val genreListBody = response.body()
                val genreList:List<Genre> = genreListBody?.genres ?: emptyList()

                setGenreListToRecyclerView(genreList)
            }

            override fun onFailure(call: Call<GenreList?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                t.message?.let { Log.e(getString(R.string.main_activity), it) }

            }
        })

    }
    /** this method set the list of genre to recyclerView
     */
    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        fragmentMoviesBinding?.rvMovieGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter(genreList,layoutList,firstTabName)
        }
        fragmentMoviesBinding?.rvMovieGenreList?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    /**
     *  this method will add views if list is not empty else fetch the data and then add the view
     */
    private fun addView(){
        if(popularMovieList.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.popular),popularMovieList)
        } else {
            getPopularMovies()
        }
        if(nowPlayingList.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.now_playing), nowPlayingList)
        } else {
            getNowPlayingMovies()
        }
        if(recommendedMovieList.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.recommendations),recommendedMovieList)
        } else {
            getRecommendedMovies()
        }
        if(topRatedMovieList.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.top_rated),topRatedMovieList)
        }else {
            getTopRatedMovies()
        }
        if(upcomingMovieList.isNotEmpty()){
            addViewToLayoutList(getString(R.string.upcoming_movies), upcomingMovieList)
        }else {
            getUpComingMovies()
        }
    }

    /** this method get the list of recommended movies
     */
    private fun getRecommendedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<Recommendations?> {
            override fun onResponse(call: Call<Recommendations?>, response: Response<Recommendations?>) {
                val listBody = response.body()
                recommendedMovieList = listBody!!.results
                addViewToLayoutList(getString(R.string.recommendations),recommendedMovieList)
                addTotalMovieList(recommendedMovieList)
            }
            override fun onFailure(call: Call<Recommendations?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of upcoming movies
     */
    private fun getUpComingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getUpComingMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<Upcoming?> {
            override fun onResponse(call: Call<Upcoming?>, response: Response<Upcoming?>) {
                val listBody = response.body()
                upcomingMovieList = listBody!!.results
                addViewToLayoutList(getString(R.string.now_playing),upcomingMovieList)
                addTotalMovieList(upcomingMovieList)
            }
            override fun onFailure(call: Call<Upcoming?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of top rated movies
     */
    private fun getTopRatedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TopRated?> {
            override fun onResponse(call: Call<TopRated?>, response: Response<TopRated?>) {
                val listBody = response.body()
                topRatedMovieList = listBody!!.results
                addViewToLayoutList(getString(R.string.top_rated),topRatedMovieList)
                addTotalMovieList(topRatedMovieList)

            }
            override fun onFailure(call: Call<TopRated?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of now playing movies
     */
    private fun getNowPlayingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getNowPlayingMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<NowPlaying?> {
            override fun onResponse(call: Call<NowPlaying?>, response: Response<NowPlaying?>) {
                val listBody = response.body()
                nowPlayingList = listBody!!.results
                addViewToLayoutList(getString(R.string.now_playing),nowPlayingList)
                addTotalMovieList(nowPlayingList)

            }
            override fun onFailure(call: Call<NowPlaying?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /**
     *  this method will get the list of popular movies
     */
    private fun getPopularMovies(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<PopularMovies?> {
            override fun onResponse(call: Call<PopularMovies?>, response: Response<PopularMovies?>) {
                val listBody = response.body()
                popularMovieList = listBody!!.results
                addViewToLayoutList(getString(R.string.popular), popularMovieList)
                addTotalMovieList(popularMovieList)
            }
            override fun onFailure(call: Call<PopularMovies?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(getString(R.string.first_fragment),t.message.toString())
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
        layoutList.addView(llView)
        val relativeLayout:RelativeLayout = llView.findViewById(R.id.rl_add_item)
        relativeLayout.setOnClickListener{
            val intent = Intent(context, ShowCategory::class.java)
            val bundle = Bundle()
            bundle.putSerializable(getString(R.string.key_category_list), movieList as Serializable)
            bundle.putString(getString(R.string.key_category_name), textString)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    /**
     * this will set the categoryList list of movies in recyclerView
     */
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

    private fun addTotalMovieList(movieList:List<Result>){

        for(items in movieList){
          if(!hashMap.contains(items.title)){
              totalMovieList.add(items)
              hashMap[items.title] = 1
          }
        }
    }
    interface FirstFragmentToActivity{
        fun onReceivedData(totalMovieList:List<Result>)
    }


}