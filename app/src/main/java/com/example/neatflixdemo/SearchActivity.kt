package com.example.neatflixdemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.adapter.SearchAdapter
import com.example.neatflixdemo.databinding.ActivitySearchBinding
import com.example.neatflixdemo.dataclasses.Result
import java.util.*


class SearchActivity : AppCompatActivity() {
    private lateinit var searchBinding : ActivitySearchBinding
    private lateinit var totalMovieList: List<Result>
    private var totalTvShowList = mutableListOf<Result>()
    private var mapList = HashMap<String,Result>()
    private lateinit var searchRV: RecyclerView
    private lateinit var listAdapter: SearchAdapter

    lateinit var searchView: SearchView
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        actionBar?.hide()
        val bundle: Bundle? = intent.extras
        totalMovieList = bundle?.getSerializable("movie_list") as List<Result>
        searchRV = searchBinding.rvSearch
        buildRecyclerView()

        searchView = searchBinding.searchView
        val id =
            searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val textView = searchView.findViewById<View>(id) as TextView

        textView.setTextColor(Color.WHITE)
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

        val filteredList = mutableListOf<Result>()

        // running a for loop to compare elements.
        for (item in totalMovieList) {
            if (item.title.toLowerCase().contains(text.lowercase(Locale.getDefault()))) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            listAdapter.filterList(filteredList)
        }
    }

    private fun buildRecyclerView() {

        listAdapter = SearchAdapter(totalTvShowList )
        val manager = LinearLayoutManager(this)
        searchRV.setHasFixedSize(true)
        searchRV.layoutManager = manager
        searchRV.adapter = listAdapter
    }
}


