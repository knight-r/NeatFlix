


package com.example.neatflixdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.CategoryListAdapter
import com.example.neatflixdemo.adapter.RVAddViewAdapter
import com.example.neatflixdemo.databinding.ActivityShowCategoryBinding
import com.example.neatflixdemo.dataclasses.Result

class ShowCategory : BaseActivity() {
    private lateinit var _binding : ActivityShowCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityShowCategoryBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(_binding.root)

        val bundle: Bundle? = intent.extras
        val list :List<Result> = bundle?.getSerializable(getString(R.string.key_category_list)) as List<Result>
        val categoryName:String = bundle?.getString(getString(R.string.key_category_name)) as String
        val recyclerView:RecyclerView = _binding.rvShowCategory
        _binding.tvCategoryName.text = "$categoryName List"
        setDataToRecyclerView(recyclerView,list)
        _binding.backButton.setOnClickListener{
            finish()
        }

    }
    private fun setDataToRecyclerView(recyclerView: RecyclerView, list:List<Result> ){
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = CategoryListAdapter(list)
        }


    }


    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals(getString(R.string.signup_activity))) {
            checkLoginStatus()
        }
    }
}