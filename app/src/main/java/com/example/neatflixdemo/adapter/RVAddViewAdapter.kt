package com.example.neatflixdemo.adapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.neatflixdemo.R
import com.example.neatflixdemo.activities.ShowDetailsActivity
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Result
import java.io.Serializable

class RVAddViewAdapter(private val mList:List<Result>): RecyclerView.Adapter<RVAddViewAdapter.ViewHolder>() {
   private lateinit var _context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _context = parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_image_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: Result = mList[position]
        holder.imageView.load(Constants.API_TMDB_IMAGE_BASE_URL + model.poster_path) {
            crossfade(true)
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
       val imageView:ImageView = itemView.findViewById(R.id.iv_add_poster)

    }
}