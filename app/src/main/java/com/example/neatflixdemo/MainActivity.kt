package com.example.neatflixdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.neatflixdemo.adapter.ViewPagerAdapter
import com.example.neatflixdemo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        var tabNameList:List<String> = listOf("Movies","TvShows")

        setupViewPager(tabNameList)
        setupTabLayout(tabNameList)

    }


    private fun setupTabLayout(tabNameList:List<String>) {
        TabLayoutMediator(
            mainBinding.tabLayout, mainBinding.viewPager
        ) { tab, position -> tab.text = tabNameList[position]}.attach()

    }
    /**
     * This viewpager is responsible for setting up view pager in main activity.
     * */
    private fun setupViewPager(tabNameList:List<String>) {
        val viewPagerAdapter = ViewPagerAdapter(this, tabNameList?.size ?: 0)
        mainBinding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
        }
    }

    /**
     * OnBackpressed method is overridden from Activity class, Here we are handling back button click while being on user page.
     * */
    override fun onBackPressed() {
        mainBinding.viewPager?.let {
            if(it.currentItem != 0){
                it.currentItem = it.currentItem -1
            }
        }
        super.onBackPressed()
    }
}