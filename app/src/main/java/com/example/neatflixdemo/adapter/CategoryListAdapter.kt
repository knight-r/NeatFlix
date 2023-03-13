package com.example.neatflixdemo.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ShowDetailsActivity
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Result
import java.io.Serializable

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
               if(model.release_date != null){
                   releaseDateTv.text = "Released on: " + model.release_date
               }else{
                   releaseDateTv.visibility = View.GONE
               }
//               model.release_date?.let {
//               }
               model.vote_average.let {
                   ratingTV.text = "Rating: " + model.vote_average
               }

           }
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
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_category_list)
        val nameTV : TextView = itemView.findViewById(R.id.tv_category_list_name)
        val releaseDateTv : TextView = itemView.findViewById(R.id.tv_category_list_release_date)
        val ratingTV : TextView = itemView.findViewById(R.id.tv_category_list_rating)

    }
}