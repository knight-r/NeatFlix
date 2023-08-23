package com.example.neatflixdemo.fragments
import android.app.ProgressDialog
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
import androidx.lifecycle.ViewModelProvider
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
import com.example.neatflixdemo.network.ResourceNotifier
import com.example.neatflixdemo.network.RetrofitClient
import com.example.neatflixdemo.repository.MovieRepository
import com.example.neatflixdemo.repository.TvShowRepository
import com.example.neatflixdemo.utils.Utils
import com.example.neatflixdemo.viewmodel.MovieViewModel
import com.example.neatflixdemo.viewmodel.MovieViewModelFactory
import com.example.neatflixdemo.viewmodel.TvShowViewModel
import com.example.neatflixdemo.viewmodel.TvShowViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class TvShowsFragment : Fragment(), RVGenreAdapter.AdapterToFragment {
    private var tvShowFragmentBinding:FragmentTvshowsBinding?=null
    private lateinit var layoutListTv: LinearLayout
    private lateinit var tvShowData: TvShowData
    private var totalTvShowList = mutableListOf<Result>()
    private var hashMap = mutableMapOf<String,Int>()
    private lateinit var tvShowViewModel: TvShowViewModel
    private  var genreItemPosition: Int =0
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        progressDialog = ProgressDialog(context)
        tvShowFragmentBinding = FragmentTvshowsBinding.inflate(layoutInflater,container,false)
        layoutListTv = tvShowFragmentBinding!!.layoutListTv
        tvShowData = TvShowData(emptyList(), emptyList(), emptyList(), emptyList(), mutableListOf(),
            emptyList()
        )
        (activity as DashboardActivity?)?.onReceivedTvShowsData(totalTvShowList)
        val retrofitClient = RetrofitClient.getInstance()
        val dataService = retrofitClient!!.create(GetDataService::class.java)
        tvShowViewModel = ViewModelProvider(this, TvShowViewModelFactory(TvShowRepository(dataService)))[TvShowViewModel::class.java]
        getTvGenres()

        progressDialog.setMessage("Loading...")
        tvShowFragmentBinding!!.refreshLayout.setOnRefreshListener {
            layoutListTv.removeAllViewsInLayout()
            addView(tvShowData.genres[genreItemPosition].id)
            tvShowFragmentBinding!!.refreshLayout.isRefreshing = false
        }
        return tvShowFragmentBinding?.root

    }

    override fun getGenreItemPosition(position: Int) {
        genreItemPosition = position
        if(tvShowData.genres.isNotEmpty()){
            layoutListTv.removeAllViewsInLayout()
            addView(tvShowData.genres[position].id)
        }
    }

    /**
     * this will add view to layout list
     */
    private fun addView(genreId: Int) {
        hashMap.clear()
        progressDialog.show()
        if(tvShowData.popularTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.popular),tvShowData.popularTvShows, genreId)
        } else {
            getPopularTvShows(genreId)
        }
        if(tvShowData.recommendedTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.recommendations),tvShowData.recommendedTvShows,genreId)
        } else {
            getRecommendedTvShows(genreId)
        }
        if(tvShowData.topRatedTvShows.isNotEmpty()) {
            addViewToLayoutList(getString(R.string.top_rated),tvShowData.topRatedTvShows,genreId)
        }else {
            getTopRatedTvShows(genreId)
        }
        if(tvShowData.tvAiringToday.isNotEmpty()){
            addViewToLayoutList(getString(R.string.tv_airing_today), tvShowData.tvAiringToday, genreId)
        }else {
            getTvAiringToday(genreId)
        }
        progressDialog.cancel()

    }
    /** this method get the list of TvShows genres
     */
    private fun getTvGenres(){
        tvShowViewModel.getTvShowGenres()
        tvShowViewModel.tvShowGenres.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    tvShowData.genres = resource.data!!.genres
                    setGenreListToRecyclerView(tvShowData.genres)
                    addView(tvShowData.genres[0].id)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }

    }

    /**
     * this method sets the genreList to recyclerView
     */
    private fun setGenreListToRecyclerView(genreList: List<Genre>) {
        tvShowFragmentBinding?.rvTvGenreList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RVGenreAdapter(MovieFragment(),this@TvShowsFragment,genreList, "")
        }
        tvShowFragmentBinding?.rvTvGenreList?.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    /** this method get the list of top rated TvShows
     */
    private fun getTopRatedTvShows(genreId: Int) {
        tvShowViewModel.getTopRatedTvShows()
        tvShowViewModel.topRatedTvShows.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    tvShowData.topRatedTvShows = resource.data!!.results
                    addViewToLayoutList(getString(R.string.top_rated), resource.data.results, genreId )
                    addTotalTvShowList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of popular TvShows
     */
    private fun getPopularTvShows(genreId: Int){
        tvShowViewModel.getPopularTvShows()
        tvShowViewModel.popularTvShows.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    tvShowData.popularTvShows = resource.data!!.results
                    addViewToLayoutList(getString(R.string.popular), resource.data.results,genreId )
                    addTotalTvShowList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of TvShows Airing today
     */
    private fun getTvAiringToday(genreId: Int){
        tvShowViewModel.getTvAiringToday()
        tvShowViewModel.tvAiringToday.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    tvShowData.tvAiringToday = resource.data!!.results
                    addViewToLayoutList(getString(R.string.tv_airing_today), resource.data.results,genreId )
                    addTotalTvShowList(resource.data.results)
                }
                is ResourceNotifier.Error -> {
                    Log.e("Response Error: ", resource?.message.toString())
                }
            }
        }
    }

    /** this method get the list of recommended TvShows
     */
    private fun getRecommendedTvShows(genreId: Int) {
        tvShowViewModel.getRecommendedTvShows()
        tvShowViewModel.recommendedtvShows.observe(viewLifecycleOwner){resource ->
            when(resource){
                is ResourceNotifier.Loading -> {
                    Log.e("Response loading: ","loading")
                }
                is ResourceNotifier.Success -> {
                    tvShowData.recommendedTvShows = resource.data!!.results
                    addViewToLayoutList(getString(R.string.recommendations), resource.data.results,genreId )
                    addTotalTvShowList(resource.data.results)
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
    private fun addViewToLayoutList(textString:String, tvShowList:List<Result>, genreId: Int){
        var newTvShowList = mutableListOf<Result>()
        for(i in tvShowList.indices){
            if(tvShowList[i].genre_ids.contains(genreId)){
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
    private fun addTotalTvShowList(tvList:List<Result>){
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