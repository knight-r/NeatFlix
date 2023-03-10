package com.example.neatflixdemo.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ShowDetailsActivity
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Result
import java.io.Serializable


class SearchAdapter( mList:List<Result>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private lateinit var _context: Context
    private var mList =  mutableListOf<Result>()
    fun filterList(filterList: List<Result>) {
        mList = filterList as MutableList<Result>
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Result = mList[position]
        holder.apply {
            releaseDateTV.text = model.release_date
            searchItemIV.load(Constants.API_TMDB_IMAGE_BASE_URL + model.poster_path) {
                crossfade(true)
            }
            model.title?.let {
                titleTV.text = model.title
            }
            model.name?.let {
                titleTV.text = model.name
            }



            ratingBar.rating = model.vote_average.toFloat() / 2

        }
        holder.itemView.setOnClickListener{
            val intent = Intent(_context, ShowDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("result_data", model as Serializable)

            intent.putExtras(bundle)
            _context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        // returning the size of array list.
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchItemIV:ImageView = itemView.findViewById(R.id.iv_search_item)
        val titleTV: TextView = itemView.findViewById(R.id.tv_name_search_item)
        val releaseDateTV:TextView = itemView.findViewById(R.id.tv_release_date_search_item)
        val ratingBar:RatingBar = itemView.findViewById(R.id.rating_bar)
        val genreNameTV:TextView = itemView.findViewById(R.id.tv_genre_search_item)

    }

    init {
        this.mList = mList as MutableList<Result>
    }
}