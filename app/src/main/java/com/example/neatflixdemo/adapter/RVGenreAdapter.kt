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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ErrorPageActivity
import com.example.neatflixdemo.activities.ShowCategory
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentMoviesBinding
import com.example.neatflixdemo.databinding.RowGenreItemBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.utils.Utils
import com.google.android.exoplayer2.util.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class RVGenreAdapter(private val genreList: List<Genre>, private val layoutList:LinearLayout,
                     val mTabName:String) : RecyclerView.Adapter<RVGenreAdapter.ViewHolder>() {
    private var _context: Context? = null
    private lateinit var _binding: RowGenreItemBinding
    private lateinit var firstFragmentBinding: FragmentMoviesBinding
    private   var selectedPosition:Int=0
    private lateinit var layout_list:LinearLayout
    private val tabName:String = DashboardTabList.MOVIES.name
    private var genreId: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        layout_list = this.layoutList
        firstFragmentBinding = FragmentMoviesBinding.inflate(LayoutInflater.from(parent.context))
        _binding = RowGenreItemBinding.inflate(LayoutInflater.from(parent.context))

        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_genre_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        holder.textViewGenre.text = genreList[position].name
        holder.itemView.setOnClickListener {
            genreId = genreList[position].id
            selectedPosition = position
            layout_list.removeAllViewsInLayout()
            if(mTabName == tabName){
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

        dataService?.getTopRatedTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TopRatedTvShows?> {
            override fun onResponse(call: Call<TopRatedTvShows?>, response: Response<TopRatedTvShows?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.top_rated) , tvShowList)

            }
            override fun onFailure(call: Call<TopRatedTvShows?>, t: Throwable) {
                Log.e(_context!!.getString(R.string.second_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of popular TvShows for selected genre
     */
    private fun getPopularTvShows(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<PopularTvShows?> {
            override fun onResponse(call: Call<PopularTvShows?>, response: Response<PopularTvShows?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.popular), tvShowList)

            }
            override fun onFailure(call: Call<PopularTvShows?>, t: Throwable) {
                Log.e(_context!!.getString(R.string.second_fragment),t.message.toString())
            }
        })

    }

    /** this method get the list of TvShows Airing today for selected genre
     */
    private fun getTvAiringToday(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTvAiringToday(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TvAiringToday?> {
            override fun onResponse(call: Call<TvAiringToday?>, response: Response<TvAiringToday?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.tv_airing_today), tvShowList)

            }
            override fun onFailure(call: Call<TvAiringToday?>, t: Throwable) {
                Log.e(_context!!.getString(R.string.second_fragment),t.message.toString())
            }
        })

    }

    /** this method get the list of recommended TvShows for selected genre
     */
    private fun getRecommendedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<RecommendedTvShows?> {
            override fun onResponse(call: Call<RecommendedTvShows?>, response: Response<RecommendedTvShows?>) {
                val listBody = response.body()
                val tvShowList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.recommendations), tvShowList)

            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                Log.e(_context!!.getString(R.string.second_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of popular movies for the selected genre
     */
    private fun getPopularMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<PopularMovies?> {
            override fun onResponse(call: Call<PopularMovies?>, response: Response<PopularMovies?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.popular), movieList)

            }
            override fun onFailure(call: Call<PopularMovies?>, t: Throwable) {
                Log.e(_context!!.getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of recommended movies for the selected genre
     */
    private fun getRecommendedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<RecommendedMovies?> {
            override fun onResponse(call: Call<RecommendedMovies?>, response: Response<RecommendedMovies?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.recommendations), movieList)

            }
            override fun onFailure(call: Call<RecommendedMovies?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e(_context!!.getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of upcoming movies for the selected genre
     */
    private fun getUpComingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getUpComingMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<UpcomingMovies?> {
            override fun onResponse(call: Call<UpcomingMovies?>, response: Response<UpcomingMovies?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.upcoming_movies), movieList)

            }
            override fun onFailure(call: Call<UpcomingMovies?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e(_context!!.getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of top rated movies for the selected genre
     */
    private fun getTopRatedMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTopRatedMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TopRatedMovies?> {
            override fun onResponse(call: Call<TopRatedMovies?>, response: Response<TopRatedMovies?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.top_rated), movieList)

            }
            override fun onFailure(call: Call<TopRatedMovies?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e(_context!!.getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    /** this method get the list of now playing movies for the selected genre
     */
    private fun getNowPlayingMovies() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getNowPlayingMovies(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<NowPlayingMovies?> {
            override fun onResponse(call: Call<NowPlayingMovies?>, response: Response<NowPlayingMovies?>) {
                val listBody = response.body()
                val movieList: List<Result> = listBody!!.results
                addViewToLayoutList(_context!!.getString(R.string.now_playing), movieList)
            }
            override fun onFailure(call: Call<NowPlayingMovies?>, t: Throwable) {
                _context?.startActivity(Intent(_context, ErrorPageActivity::class.java))
                Log.e(_context!!.getString(R.string.first_fragment),t.message.toString())
            }
        })
    }

    fun addViewToLayoutList(textString:String, movieList:List<Result>){
        var newMoviesList = mutableListOf<Result>()
        for(i in movieList.indices){
            if(movieList[i].genre_ids.contains(genreId)){
                newMoviesList.add(movieList[i])
            }
        }
        val llView: View = LayoutInflater.from(_context).inflate(R.layout.row_add_item,null,false)
        val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
        textView.text = textString
        val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
        setDataToRecyclerView(recyclerView, movieList)
        if(newMoviesList.isNotEmpty()){
            layoutList.addView(llView)
        }
        val relativeLayout: RelativeLayout = llView.findViewById(R.id.rl_add_item)
        relativeLayout.setOnClickListener{
            val intent = Intent(_context , ShowCategory::class.java)
            val bundle = Bundle()
            bundle.putSerializable(_context?.getString(R.string.key_category_list), movieList as Serializable)
            bundle.putString(_context?.getString(R.string.key_category_name), textString)
            intent.putExtras(bundle)
            _context?.startActivity(intent)
        }
    }

    /** this method accepts the recyclerView of particular View item of layout List and list of results and sets
     * the list of Results to recyclerView
     * @param recyclerView :
     * @param popularMovieList :
     */
    private fun setDataToRecyclerView(recyclerView:RecyclerView, list:List<Result> ){
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVAddViewAdapter(list)
        }
        recyclerView.layoutManager = LinearLayoutManager(
            _context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

}