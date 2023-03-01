package com.example.neatflixdemo.adapter
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.neatflixdemo.R
import com.example.neatflixdemo.ShowDetailsActivity
import com.example.neatflixdemo.dataclasses.Result

class RVAddViewAdapter(private val mList:List<Result>): RecyclerView.Adapter<RVAddViewAdapter.ViewHolder>() {
   private lateinit var _context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_image_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.load("https://image.tmdb.org/t/p/original"+ mList[position].poster_path) {
            crossfade(true)
        }
       holder.itemView.setOnClickListener{
           val intent = Intent(_context, ShowDetailsActivity::class.java)
           val bundle = Bundle()
           bundle.putString("result_image", mList[position].poster_path)
           bundle.putString("result_overview",mList[position].overview)
           intent.putExtras(bundle)
           _context.startActivity(intent)
       }
    }
    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
       val imageView:ImageView = itemView.findViewById(R.id.iv_add_poster)
    }
}