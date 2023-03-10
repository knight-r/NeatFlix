package com.example.neatflixdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Result

class CategoryListAdapter(private val mList:List<Result>): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private lateinit var _context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Result = mList[position]
       if(model.poster_path != ""){
           holder.apply {
               imageView.load(Constants.API_TMDB_IMAGE_BASE_URL + model.poster_path) {
                   crossfade(true)
               }
               model.name?.let {
                   nameTV.text = model.name
               }
               model.title?.let {
                   nameTV.text = model.title
               }
               model.release_date?.let {
                   releaseDateTv.text = "Released on: " + model.release_date
               }
               ratingTV.text = "Rating: " + model.vote_average
           }
       }


    }
    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_category_list)
        val nameTV : TextView = itemView.findViewById(R.id.tv_category_list_name)
        val releaseDateTv : TextView = itemView.findViewById(R.id.tv_category_list_release_date)
        val ratingTV : TextView = itemView.findViewById(R.id.tv_category_list_rating)



    }
}