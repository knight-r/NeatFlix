package com.example.neatflixdemo.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.DashboardActivity
import com.example.neatflixdemo.activities.ShowCategory
import com.example.neatflixdemo.adapter.RVAddViewAdapter
import com.example.neatflixdemo.adapter.RVGenreAdapter
import com.example.neatflixdemo.databinding.FragmentMoviesBinding
import com.example.neatflixdemo.databinding.RowAddItemBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.repository.MovieRepository
import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.viewmodel.MovieViewModel
import com.example.neatflixdemo.viewmodel.MovieViewModelFactory
import com.example.neatflixdemo.viewmodel.TvShowViewModel
import java.io.Serializable


class MovieFragment : Fragment(), RVGenreAdapter.AdapterToFragment {
    private  var fragmentMoviesBinding:FragmentMoviesBinding?= null
    private lateinit var llViewBinding: RowAddItemBinding
    private lateinit var layoutList: LinearLayout
    private lateinit var movieData : MovieData
    private var totalMovieList = mutableListOf<Result>()
    private var hashMap = mutableMapOf<String,Int>()
    private lateinit var movieViewModel: MovieViewModel
    private var genreItemPosition: Int = 0
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        progressDialog = ProgressDialog(context)
        fragmentMoviesBinding = FragmentMoviesBinding.inflate(layoutInflater,container,false)
        llViewBinding = RowAddItemBinding.inflate(layoutInflater,container,false)
        layoutList = fragmentMoviesBinding!!.layoutList
        movieData = MovieData(emptyList(),emptyList(),emptyList(),emptyList(),emptyList(), mutableListOf(), emptyList())
        (activity as DashboardActivity?)?.onReceivedMoviesData(totalMovieList)
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)
        movieViewModel = ViewModelProvider(this, MovieViewModelFactory(MovieRepository(dataService)))[MovieViewModel::class.java]
        getMovieGenre()

        fragmentMoviesBinding!!.refreshLayout.setOnRefreshListener {
            layoutList.removeAllViewsInLayout()
            addView(movieData.genres[genreItemPosition].id)
            fragmentMoviesBinding!!.refreshLayout.isRefreshing = false
        }
        return fragmentMoviesBinding?.root
    }

    override fun getGenreItemPosition(position: Int) {
        if(movieData.genres.isNotEmpty()){
            layoutList.removeAllViewsInLayout()
            addView(movieData.genres[position].id)
        }

    }

    /** this method get the list of genres name
     */
    private fun getMovieGenre(){
        movieViewModel.getMovieGenres()
        movieViewModel.movieGenres.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    movieData.genres = resource.data!!.genres

                    /*
                    var genreList: MutableList<Genre> = mutableListOf()
                    genreList.add(Genre(-1,"All"))
                    for( items in movieData.genres) {
                        genreList.add(items)
                    }
                     */
                    setGenreListToRecyclerView(movieData.genres)
                    addView(movieData.genres[0].id)
                    Log.e("Response Success: ", resource.data.toString())
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }

    }

    /** this method set the list of genre to recyclerView
     */
    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        fragmentMoviesBinding?.rvMovieGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter( this@MovieFragment,TvShowsFragment(),genreList, DashboardTabList.MOVIES.name)
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
    private fun addView(genreId: Int){
        progressDialog.setTitle("Loading...")
        progressDialog.setMessage("Loading...")
        progressDialog.show()
        if(movieData.popularMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.popular),movieData.popularMovies, genreId)
        } else {
            getPopularMovies(genreId)
        }
        if(movieData.nowPlayingMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.now_playing), movieData.nowPlayingMovies,genreId)
        } else {
            getNowPlayingMovies(genreId)
        }
        if(movieData.recommendedMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.recommendations),movieData.recommendedMovies,genreId)
        } else {
            getRecommendedMovies(genreId)
        }
        if(movieData.topRatedMovies.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.top_rated),movieData.topRatedMovies,genreId)
        }else {
            getTopRatedMovies(genreId)
        }
        if(movieData.upcomingMovies.isNotEmpty()){
            addViewToLayoutList(getString(R.string.upcoming_movies), movieData.upcomingMovies,genreId)
        }else {
            getUpComingMovies(genreId)
        }
        progressDialog.cancel()
    }

    /** this method get the list of recommended movies
     */
    private fun getRecommendedMovies(genreId: Int) {
        movieViewModel.getRecommendedMovies()
        movieViewModel.recommendedMovies.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    Log.e("Response Success: ", resource.data.toString())
                    movieData.recommendedMovies = resource.data!!.results
                    addViewToLayoutList(getString(R.string.recommendations), resource.data.results, genreId )
                    addTotalMovieList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of upcoming movies
     */
    private fun getUpComingMovies(genreId: Int) {
        movieViewModel.getUpcomingMovies()
        movieViewModel.upcomingMovies.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    movieData.upcomingMovies = resource.data!!.results
                    addViewToLayoutList(getString(R.string.upcoming_movies), resource.data.results, genreId)
                    addTotalMovieList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of top rated movies
     */
    private fun getTopRatedMovies(genreId: Int) {
        movieViewModel.getTopRatedMovies()
        movieViewModel.topRatedMovies.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    movieData.topRatedMovies = resource.data!!.results
                    addViewToLayoutList(getString(R.string.top_rated), resource.data.results, genreId )
                    addTotalMovieList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of now playing movies
     */
    private fun getNowPlayingMovies(genreId: Int) {
        movieViewModel.getNowPlayingMovies()
        movieViewModel.nowPlayingMovies.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    movieData.nowPlayingMovies = resource.data!!.results
                    addViewToLayoutList(getString(R.string.now_playing), resource.data.results, genreId )
                    addTotalMovieList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /**
     *  this method will get the list of popular movies
     */
    private fun getPopularMovies(genreId: Int){
        movieViewModel.getPopularMovies()
        movieViewModel.popularMovies.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    movieData.popularMovies = resource.data!!.results
                    addViewToLayoutList(getString(R.string.popular), resource.data.results,genreId )
                    addTotalMovieList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /**
     *  this method add view item in layout list
     */
    private fun addViewToLayoutList(textString:String, movieList:List<Result>, movieGenreId: Int){
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