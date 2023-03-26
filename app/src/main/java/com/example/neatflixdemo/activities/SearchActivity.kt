package com.example.neatflixdemo.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Html
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
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
import org.jetbrains.annotations.Nullable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class SearchActivity : BaseActivity(), OnClickListener{
    private lateinit var searchBinding : ActivitySearchBinding
    private lateinit var totalMovieList: List<Result>
    private  var totalTvShowList = mutableListOf<Result>()
    private  var totalList = mutableListOf<Result>()
    private lateinit var searchRV: RecyclerView
    private lateinit var listAdapter: SearchAdapter
    private var hashMap = HashMap<String,Int>()
    private lateinit var searchView: SearchView
    private val REQUEST_CODE_SPEECH_INPUT:Int = 1
    private val TAG:String = "SearchActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)

        setContentView(searchBinding.root)
        searchRV = searchBinding.rvSearch
        searchView = searchBinding.searchView
        searchView.isIconified = false

        actionBar?.hide()

        getTotalSearchList()

        buildRecyclerView()
        searchView.queryHint = Html.fromHtml("<font color = #C7B9B9>" + "Search movies and Tv Shows" + "</font>")

        manageSearchViewUI()

        queryOnInput()

        searchBinding.backButton.setOnClickListener {
            finish()
        }
        searchBinding.ivMicIcon.setOnClickListener {
            enableVoiceSearch()
        }

    }

    /**
     *  this method  get total movies list and shows list in single search list
     */
    private fun getTotalSearchList() {
        val bundle: Bundle? = intent.extras
        totalMovieList = bundle?.getSerializable(Constants.KEY_MOVIE_LIST) as List<Result>
        totalTvShowList = (bundle?.getSerializable(Constants.KEY_TVSHOW_LIST) as List<Result>).toMutableList()

        for(items in totalMovieList){
            totalList.add(items)
        }
        if(totalTvShowList == emptyList<Result>() || totalTvShowList.size == 0) {
            getTotalTvShowList()
        } else {
            for(items in totalTvShowList) {
                totalList.add(items)
            }
        }
    }

    /**
     * this method is used to customize the UI of SearchView
     */
    private fun manageSearchViewUI() {
        val idTextView = searchView.context.resources.getIdentifier(getString(R.string.searchview_src_text_name), null, null)
        val textView = searchView.findViewById<View>(idTextView) as TextView
        textView.setTextColor(Color.WHITE)
        val idCloseButton = searchView.context.resources.getIdentifier(getString(R.string.searchview_close_button_name), null, null)
        val closeButton:ImageView = searchView.findViewById<View>(idCloseButton) as ImageView
        closeButton.setOnClickListener {
            val idCloseButton = searchView.context.resources.getIdentifier(getString(R.string.searchview_edit_text_name), null, null)
            val et:EditText = searchView.findViewById<View>(idCloseButton) as EditText
            et.setText("")
        }
    }

    private fun enableVoiceSearch() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_to_text))

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(
                this@SearchActivity, getString(R.string.some_error_occured) + e.message,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        @Nullable data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                )
                result?.let {
                    searchView.setQuery(it[0].toString(), true)
                }
            }
        }
    }

    /**
     * this method search the list of items based on texts entered
     */
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

    /**
     * this method filters the list of items based on text entered and generate the final list
     * @param: entered text in EditText
     */
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

    /**
     *  this method sets search list objects in RecyclerView
     */
    private fun buildRecyclerView() {
        listAdapter = SearchAdapter(totalList )
        val manager = LinearLayoutManager(this)
        searchRV.apply{
            setHasFixedSize(true)
            layoutManager = manager
            adapter = listAdapter

        }
    }

    /**
     * this method gets the list of all categories of tvShow lists
     */
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
                Log.e(TAG,t.message.toString())
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
                Log.e(TAG ,t.message.toString())
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
                Log.e(TAG,t.message.toString())
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
                Log.e(TAG,t.message.toString())
            }
        })
    }

    /**
     * this method adds list of all categories in single list with unique items
     */
    fun addTotalTvShowList(tvList:List<Result>){
        for(items in tvList){
            if(!hashMap.contains(items.name)){
                hashMap[items.name] = 1
                totalList.add(items)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals("SignUpActivity")) {
            checkLoginStatus()
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}


