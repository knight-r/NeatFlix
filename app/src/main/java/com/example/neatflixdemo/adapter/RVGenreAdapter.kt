package com.example.neatflixdemo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ErrorPageActivity
import com.example.neatflixdemo.activities.ShowCategory
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentFirstBinding
import com.example.neatflixdemo.databinding.RowGenreItemBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class RVGenreAdapter(private val genreList: List<Genre>, private val layoutList:LinearLayout,
                     val tabName:String) : RecyclerView.Adapter<RVGenreAdapter.ViewHolder>() {
    private var _context: Context? = null
    private lateinit var _binding: RowGenreItemBinding
    private lateinit var firstFragmentBinding: FragmentFirstBinding
    private   var selectedPosition:Int=0
    private var genreId:Int = genreList[0].id
    private lateinit var layout_list:LinearLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        layout_list = this.layoutList
        firstFragmentBinding = FragmentFirstBinding.inflate(LayoutInflater.from(parent.context))
        _binding = RowGenreItemBinding.inflate(LayoutInflater.from(parent.context))

        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_genre_item, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.textViewGenre.text = genreList[position].name

        holder.itemView.setOnClickListener {
            genreId = genreList[position].id
            selectedPosition = position
            layout_list.removeAllViewsInLayout()
            if(tabName == "Movies"){
                addViewInMovies()
            }else{
                addViewInTvShows()
            }
            notifyDataSetChanged()
        }

        if(selectedPosition == position) {
            holder.textViewGenre.setBackgroundResource(R.drawable.item_change_color)
        }else {

            holder.textViewGenre.setBackgroundResource(R.drawable.round_edge_item)

        }

    }
    override fun getItemCount(): Int {
        return genreList?.size ?: 0
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewGenre: TextView = itemView.findViewById(R.id.tv_genre)
    }
    private fun addViewInMovies() {
        getPopularMovies()
        getRecommendedMovies()
        getNowPlayingMovies()
        getTopRatedMovies()
        getUpComingMovies()
    }
    private fun addViewInTvShows() {
        getTopRatedTvShows()
        getPopularTvShows()
        getTvAiringToday()
        getRecommendedTvShows()
    }

    /** this method get the list of top rated TvShows for selected genre
     */
    private fun getTopRatedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TopRatedTvShows?> {
            override fun onResponse(call: Call<TopRatedTvShows?>, response: Response<TopRatedTvShows?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                var newTvShowList = mutableListOf<Result>()
                for(i in tvShowList.indices){
                    if(tvShowList[i].genre_ids.contains(genreId)){
                        newTvShowList.add(tvShowList[i])
                    }
                }
                if(newTvShowList.isNotEmpty()){
                    addViewToLayoutList("Top Rated", newTvShowList)
                }
            }
            override fun onFailure(call: Call<TopRatedTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of popular TvShows for selected genre
     */
    private fun getPopularTvShows(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<PopularTvShows?> {
            override fun onResponse(call: Call<PopularTvShows?>, response: Response<PopularTvShows?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                var newTvShowList = mutableListOf<Result>()
                for(i in tvShowList.indices){
                    if(tvShowList[i].genre_ids.contains(genreId)){
                        newTvShowList.add(tvShowList[i])
                    }
                }
                if(newTvShowList.isNotEmpty()){
                    addViewToLayoutList("Popular", newTvShowList)
                }
            }
            override fun onFailure(call: Call<PopularTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })

    }

    /** this method get the list of TvShows Airing today for selected genre
     */
    private fun getTvAiringToday(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTvAiringToday(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TvAiringToday?> {
            override fun onResponse(call: Call<TvAiringToday?>, response: Response<TvAiringToday?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                var newTvShowList = mutableListOf<Result>()
                for(i in tvShowList.indices){
                    if(tvShowList[i].genre_ids.contains(genreId)){
                        newTvShowList.add(tvShowList[i])
                    }
                }
                if(newTvShowList.isNotEmpty()){
                    addViewToLayoutList("Tv Airing Today", newTvShowList)
                }
            }
            override fun onFailure(call: Call<TvAiringToday?>, t: Throwable) {
                Log.e("Adapter: ",t.message.toString())
            }
        })

    }

    /** this method get the list of recommended TvShows for selected genre
     */
    private fun getRecommendedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedTvShows(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<RecommendedTvShows?> {
            override fun onResponse(call: Call<RecommendedTvShows?>, response: Response<RecommendedTvShows?>) {
                val listBody = response.body()

                val tvShowList: List<Result> = listBody!!.results
                var newTvShowList = mutableListOf<Result>()
                for(i in tvShowList.indices){
                    if(tvShowList[i].genre_ids.contains(genreId)){
                        newTvShowList.add(tvShowList[i])
                    }
                }
                if(newTvShowList.isNotEmpty()){
                    addViewToLayoutList("Recommendations", newTvShowList)
                }
            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                Log.e("SecondFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of popular movies for the selected genre
     */
    private fun getPopularMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<PopularMovies?> {
            override fun onResponse(call: Call<PopularMovies?>, response: Response<PopularMovies?>) {
                val listBody = response.body()
                val popularMovieList: List<Result> = listBody!!.results
                var newMoviesList = mutableListOf<Result>()
                for(i in popularMovieList.indices){
                    if(popularMovieList[i].genre_ids.contains(genreId)){
                        newMoviesList.add(popularMovieList[i])
                    }
                }
                if(newMoviesList.isNotEmpty()){
                    addViewToLayoutList("Popular", newMoviesList)
                }
            }
            override fun onFailure(call: Call<PopularMovies?>, t: Throwable) {
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of recommended movies for the selected genre
     */
    private fun getRecommendedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<Recommendations?> {
            override fun onResponse(call: Call<Recommendations?>, response: Response<Recommendations?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                var newMoviesList = mutableListOf<Result>()
                for(i in movieList.indices){
                    if(movieList[i].genre_ids.contains(genreId)){
                        newMoviesList.add(movieList[i])
                    }
                }
                if(newMoviesList.isNotEmpty()){
                    addViewToLayoutList("Recommendations", newMoviesList)
                }
            }
            override fun onFailure(call: Call<Recommendations?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of upcoming movies for the selected genre
     */
    private fun getUpComingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getUpComingMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<Upcoming?> {
            override fun onResponse(call: Call<Upcoming?>, response: Response<Upcoming?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                var newMoviesList = mutableListOf<Result>()
                for(i in movieList.indices){
                    if(movieList[i].genre_ids.contains(genreId)){
                        newMoviesList.add(movieList[i])
                    }
                }
                if(newMoviesList.isNotEmpty()){
                    addViewToLayoutList("Upcoming Movies", newMoviesList)
                }
            }
            override fun onFailure(call: Call<Upcoming?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of top rated movies for the selected genre
     */
    private fun getTopRatedMovies() {

        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<TopRated?> {
            override fun onResponse(call: Call<TopRated?>, response: Response<TopRated?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                var newMoviesList = mutableListOf<Result>()
                for(i in movieList.indices){
                    if(movieList[i].genre_ids.contains(genreId)){
                        newMoviesList.add(movieList[i])
                    }
                }
                if(newMoviesList.isNotEmpty()){
                    addViewToLayoutList("Top Rated", newMoviesList)
                }
            }
            override fun onFailure(call: Call<TopRated?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }

    /** this method get the list of now playing movies for the selected genre
     */
    private fun getNowPlayingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getNowPlayingMovies(Constants.API_KEY_TMDB,"en-US")?.enqueue(object:
            Callback<NowPlaying?> {
            override fun onResponse(call: Call<NowPlaying?>, response: Response<NowPlaying?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                var newMoviesList = mutableListOf<Result>()
                for(i in movieList.indices){
                    if(movieList[i].genre_ids.contains(genreId)){
                        newMoviesList.add(movieList[i])
                    }
                }

                if(newMoviesList.isNotEmpty()){
                    addViewToLayoutList("Now Playing", newMoviesList)
                }
            }
            override fun onFailure(call: Call<NowPlaying?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e("FirstFragment: ",t.message.toString())
            }
        })
    }
    fun addViewToLayoutList(textString:String, movieList:List<Result>){
        val llView: View = LayoutInflater.from(_context).inflate(R.layout.row_add_item,null,false)
        val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
        textView.text = textString
        val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
        setDataToRecyclerView(recyclerView, movieList)
        layoutList.addView(llView)
        val nextButton: ImageView = llView.findViewById(R.id.next_btn)
        nextButton.setOnClickListener{
            val intent = Intent(_context, ShowCategory::class.java)
            val bundle = Bundle()
            bundle.putSerializable("category_list", movieList as Serializable)
            bundle.putString("category_name", textString)
            intent.putExtras(bundle)
            _context?.startActivity(intent)
        }
    }

    /** this method accepts the recyclerView of particular View item of layout List and list of results and sets
     * the list of Results to recyclerView
     * @param recyclerView :
     * @param popularMovieList :
     * @return
     */
    private fun setDataToRecyclerView(recyclerView:RecyclerView, popularMovieList:List<Result> ){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVAddViewAdapter(popularMovieList)
        }
        recyclerView.layoutManager = LinearLayoutManager(
            _context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

}