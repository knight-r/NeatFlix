package com.example.neatflixdemo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.dataclasses.*
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.fragments.MovieFragment
import com.example.neatflixdemo.fragments.TvShowsFragment

class RVGenreAdapter(private val movieFragment: MovieFragment,
                     private val tvShowFragment: TvShowsFragment,
                     private val genreList: List<Genre>,
                     private val  mTabName: String
) : RecyclerView.Adapter<RVGenreAdapter.ViewHolder>() {
    private   var selectedPosition:Int=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_genre_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.textViewGenre.text = genreList[position].name
        holder.itemView.setOnClickListener {
            selectedPosition = position
            if (mTabName == DashboardTabList.MOVIES.name) {
                movieFragment.getGenreItemPosition(position)
            }else {
                tvShowFragment.getGenreItemPosition(position)
            }
            notifyDataSetChanged()
        }
        if (selectedPosition == position) {
            holder.textViewGenre.setBackgroundResource(R.drawable.item_change_color)
        } else {
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

    interface AdapterToFragment {
        fun getGenreItemPosition(position: Int)
    }


}