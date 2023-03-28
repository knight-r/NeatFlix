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
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class MovieFragment : Fragment() {
    private  var fragmentMoviesBinding:FragmentMoviesBinding?= null
    private lateinit var llViewBinding: RowAddItemBinding
    private lateinit var layoutList: LinearLayout
     private lateinit var movieData: MovieData
    private var totalMovieList = mutableListOf<Result>()
    private var hashMap = mutableMapOf<String,Int>()
    private val firstTabName:String = DashboardTabList.MOVIES.name
    private var movieGenreId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentMoviesBinding = FragmentMoviesBinding.inflate(layoutInflater,container,false)
        llViewBinding = RowAddItemBinding.inflate(layoutInflater,container,false)
        layoutList = fragmentMoviesBinding!!.layoutList
        movieData = MovieData(emptyList(),emptyList(),emptyList(),emptyList(),emptyList(), mutableListOf())
        getMovieGenre()

        (activity as DashboardActivity?)?.onReceivedMoviesData(totalMovieList)

        fragmentMoviesBinding!!.refreshLayout.setOnRefreshListener {
            movieGenreId = Utils.genreID
            layoutList.removeAllViewsInLayout()
            movieData =  MovieData(emptyList(),emptyList(),emptyList(),emptyList(),emptyList(), mutableListOf())
            getPopularMovies()
            getNowPlayingMovies()
            getRecommendedMovies()
            getTopRatedMovies()
            getUpComingMovies()
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
                movieGenreId = genreList[0].id
                Utils.genreID = movieGenreId
                addView()
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
        if(movieData.popularMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.popular),movieData.popularMovies)
        } else {
            getPopularMovies()
        }
        if(movieData.nowPlayingMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.now_playing), movieData.nowPlayingMovies)
        } else {
            getNowPlayingMovies()
        }
        if(movieData.recommendedMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.recommendations),movieData.recommendedMovies)
        } else {
            getRecommendedMovies()
        }
        if(movieData.topRatedMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.top_rated),movieData.topRatedMovies)
        }else {
            getTopRatedMovies()
        }
        if(movieData.upcomingMovies.isNotEmpty()){
            addViewToLayoutList(getString(R.string.upcoming_movies), movieData.upcomingMovies)
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
            Callback<RecommendedMovies?> {
            override fun onResponse(call: Call<RecommendedMovies?>, response: Response<RecommendedMovies?>) {
                val listBody = response.body()
                movieData.recommendedMovies = listBody!!.results
                addViewToLayoutList(getString(R.string.recommendations), movieData.recommendedMovies)
                addTotalMovieList( movieData.recommendedMovies)
            }
            override fun onFailure(call: Call<RecommendedMovies?>, t: Throwable) {
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
            Callback<UpcomingMovies?> {
            override fun onResponse(call: Call<UpcomingMovies?>, response: Response<UpcomingMovies?>) {
                val listBody = response.body()
                movieData.upcomingMovies = listBody!!.results
                addViewToLayoutList(getString(R.string.upcoming_movies), movieData.upcomingMovies)
                addTotalMovieList(movieData.upcomingMovies)
            }
            override fun onFailure(call: Call<UpcomingMovies?>, t: Throwable) {
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
            Callback<TopRatedMovies?> {
            override fun onResponse(call: Call<TopRatedMovies?>, response: Response<TopRatedMovies?>) {
                val listBody = response.body()
                movieData.topRatedMovies = listBody!!.results
                addViewToLayoutList(getString(R.string.top_rated),   movieData.topRatedMovies)
                addTotalMovieList(movieData.topRatedMovies)

            }
            override fun onFailure(call: Call<TopRatedMovies?>, t: Throwable) {
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
            Callback<NowPlayingMovies?> {
            override fun onResponse(call: Call<NowPlayingMovies?>, response: Response<NowPlayingMovies?>) {
                val listBody = response.body()
                movieData.nowPlayingMovies = listBody!!.results
                addViewToLayoutList(getString(R.string.now_playing),movieData.nowPlayingMovies)
                addTotalMovieList(movieData.nowPlayingMovies)

            }
            override fun onFailure(call: Call<NowPlayingMovies?>, t: Throwable) {
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
                movieData.popularMovies = listBody!!.results
                addViewToLayoutList(getString(R.string.popular), movieData.popularMovies)
                addTotalMovieList(movieData.popularMovies)
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
        var newMoviesList = mutableListOf<Result>()
        for(i in movieList.indices){
            if(movieList[i].genre_ids.contains(movieGenreId)){
                newMoviesList.add(movieList[i])
            }
        }
        val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
        val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
        textView.text = textString
        val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
        setDataToRecyclerView(recyclerView, newMoviesList)
        if(newMoviesList.isNotEmpty()){
            layoutList.addView(llView)
        }
        val relativeLayout:RelativeLayout = llView.findViewById(R.id.rl_add_item)
        relativeLayout.setOnClickListener{
            val intent = Intent(context, ShowCategory::class.java)
            val bundle = Bundle()
            bundle.putSerializable(getString(R.string.key_category_list), newMoviesList as Serializable)
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

    /**
     * it will add all category movie list in a single list with unique items
     */
    private fun addTotalMovieList(movieList:List<Result>){

        for(items in movieList){
          if(!hashMap.contains(items.title)){
              totalMovieList.add(items)
              hashMap[items.title] = 1
          }
        }
    }

    /**
     * it will send the totalMovieList to DashboardActivity
     */
    interface FirstFragmentToActivity{
        fun onReceivedMoviesData(totalMovieList:List<Result>)
    }


}