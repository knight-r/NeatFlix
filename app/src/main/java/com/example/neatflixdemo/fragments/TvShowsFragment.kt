package com.example.neatflixdemo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.activities.DashboardActivity
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ErrorPageActivity
import com.example.neatflixdemo.activities.ShowCategory
import com.example.neatflixdemo.adapter.RVAddViewAdapter
import com.example.neatflixdemo.services.GetDataService
import com.example.neatflixdemo.adapter.RVGenreAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.FragmentTvshowsBinding
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class TvShowsFragment : Fragment() {
    private var tvShowFragmentBinding:FragmentTvshowsBinding?=null
    private lateinit var layoutListTv: LinearLayout
    private lateinit var tvShowData: TvShowData
    private var totalTvShowList = mutableListOf<Result>()
    private var hashMap = mutableMapOf<String,Int>()
    private  val TAG:String = "TvShowFragment"
    private val secondTabName:String = DashboardTabList.TVSHOWS.name
    private var tvGenreId: Int = 10759
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        tvShowFragmentBinding = FragmentTvshowsBinding.inflate(layoutInflater,container,false)
        layoutListTv = tvShowFragmentBinding!!.layoutListTv
        tvShowData = TvShowData(emptyList(), emptyList(), emptyList(), emptyList(), mutableListOf())
        getTvGenreList()
        addView()
        (activity as DashboardActivity?)?.onReceivedTvShowsData(totalTvShowList)
        Utils.genreID = tvGenreId
        Log.e(TAG, Utils.genreID.toString())
        tvShowFragmentBinding!!.refreshLayout.setOnRefreshListener {
            tvGenreId = Utils.genreID
            layoutListTv.removeAllViewsInLayout()
            tvShowData = TvShowData(emptyList(), emptyList(), emptyList(),  emptyList(), mutableListOf())
            getPopularTvShows()
            getRecommendedTvShows()
            getTopRatedTvShows()
            getTvAiringToday()
            tvShowFragmentBinding!!.refreshLayout.isRefreshing = false
        }
        return tvShowFragmentBinding?.root

    }

    /**
     * this will add view to layout list
     */
    private fun addView() {
        hashMap.clear()
        if(tvShowData.popularTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.popular),tvShowData.popularTvShows)
        } else {
            getPopularTvShows()
        }
        if(tvShowData.recommendedTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.recommendations),tvShowData.recommendedTvShows)
        } else {
            getRecommendedTvShows()
        }
        if(tvShowData.topRatedTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.top_rated),tvShowData.topRatedTvShows)
        }else {
            getTopRatedTvShows()
        }
        if(tvShowData.tvAiringToday.isNotEmpty()){
            addViewToLayoutList(getString(R.string.tv_airing_today), tvShowData.tvAiringToday)
        }else {
            getTvAiringToday()
        }

    }
    /** this method get the list of TvShows genres
     */
    private fun getTvGenreList(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)
        dataService?.getTvGenres(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<GenreList?> {
            override fun onResponse(call: Call<GenreList?>, response: Response<GenreList?>) {
                val genreListBody = response.body()
                val genreList:List<Genre> = genreListBody?.genres ?: emptyList()
                setGenreListToRecyclerView(genreList)
            }
            override fun onFailure(call: Call<GenreList?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
            }
        })

    }

    /**
     * this method sets the genreList to recyclerView
     */
    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        tvShowFragmentBinding?.rvTvGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter(genreList,layoutListTv,secondTabName)
        }
        tvShowFragmentBinding?.rvTvGenreList?.layoutManager = LinearLayoutManager(
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

        dataService?.getTopRatedTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TopRatedTvShows?> {
            override fun onResponse(call: Call<TopRatedTvShows?>, response: Response<TopRatedTvShows?>) {
                val listBody = response.body()
                tvShowData.topRatedTvShows = listBody!!.results
                addViewToLayoutList(getString(R.string.top_rated),tvShowData.topRatedTvShows)
                addTotalTvShowList(tvShowData.topRatedTvShows)
            }
            override fun onFailure(call: Call<TopRatedTvShows?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(TAG,t.message.toString())
            }
        })
    }

    /** this method get the list of popular TvShows
     */
    private fun getPopularTvShows(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getPopularTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<PopularTvShows?> {
            override fun onResponse(call: Call<PopularTvShows?>, response: Response<PopularTvShows?>) {
                val listBody = response.body()
                tvShowData.popularTvShows = listBody!!.results
                addViewToLayoutList(getString(R.string.popular),tvShowData.popularTvShows)
                addTotalTvShowList(tvShowData.popularTvShows)
            }
            override fun onFailure(call: Call<PopularTvShows?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(TAG,t.message.toString())
            }
        })

    }

    /** this method get the list of TvShows Airing today
     */
    private fun getTvAiringToday(){
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getTvAiringToday(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<TvAiringToday?> {
            override fun onResponse(call: Call<TvAiringToday?>, response: Response<TvAiringToday?>) {
                val listBody = response.body()
                tvShowData.tvAiringToday = listBody!!.results
                addViewToLayoutList(getString(R.string.tv_airing_today), tvShowData.tvAiringToday)
                addTotalTvShowList( tvShowData.tvAiringToday)
            }
            override fun onFailure(call: Call<TvAiringToday?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(TAG ,t.message.toString())
            }
        })

    }

    /** this method get the list of recommended TvShows
     */
    private fun getRecommendedTvShows() {
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient?.create(GetDataService::class.java)

        dataService?.getRecommendedTvShows(Constants.API_KEY_TMDB,Constants.API_LANGUAGE)?.enqueue(object:
            Callback<RecommendedTvShows?> {
            override fun onResponse(call: Call<RecommendedTvShows?>, response: Response<RecommendedTvShows?>) {
                val listBody = response.body()
                tvShowData.recommendedTvShows = listBody!!.results
                addViewToLayoutList(getString(R.string.recommendations),tvShowData.recommendedTvShows)
                addTotalTvShowList(tvShowData.recommendedTvShows)
            }
            override fun onFailure(call: Call<RecommendedTvShows?>, t: Throwable) {
                startActivity(Intent(context, ErrorPageActivity::class.java))
                Log.e(TAG,t.message.toString())
            }
        })
    }
    /**
     *  this method add view item in layout list
     */
    fun addViewToLayoutList(textString:String, tvShowList:List<Result>){
        var newTvShowList = mutableListOf<Result>()
        for(i in tvShowList.indices){
            if(tvShowList[i].genre_ids.contains(tvGenreId)){
                newTvShowList.add(tvShowList[i])
            }
        }
        val llView: View = layoutInflater.inflate(R.layout.row_add_item, null, false)
        val textView:TextView = llView.findViewById(R.id.tv_row_add_item)
        textView.text = textString
        val recyclerView: RecyclerView = llView.findViewById(R.id.rv_row_add_item)
        setDataToRecyclerView(recyclerView, newTvShowList)
        if(newTvShowList.isNotEmpty()){
            layoutListTv.addView(llView)
        }
        val relativeLayout: RelativeLayout = llView.findViewById(R.id.rl_add_item)
        relativeLayout.setOnClickListener{
            val intent = Intent(context , ShowCategory::class.java)
            val bundle = Bundle()
            bundle.putSerializable(getString(R.string.key_category_list), tvShowList as Serializable)
            bundle.putString(getString(R.string.key_category_name), textString)
            intent.putExtras(bundle)
            startActivity(intent)
        }
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

    /**
     * it will add all category tvShow list in a single list with unique items
     */
    fun addTotalTvShowList(tvList:List<Result>){
        for(items in tvList){
            if(!hashMap.contains(items.name)){
                totalTvShowList.add(items)
                hashMap[items.name] = 1
            }
        }
    }

    /**
     * it will send the totalTvShowList to DashboardActivity
     */
    interface SecondFragmentToActivity{
        fun onReceivedTvShowsData(totalTvShowList:List<Result>)
    }


}