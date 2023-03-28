package com.example.neatflixdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.ViewPagerAdapter
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityDashboardBinding
import com.example.neatflixdemo.fragments.MovieFragment
import com.example.neatflixdemo.dataclasses.Result
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.fragments.TvShowsFragment
import com.google.android.material.tabs.TabLayoutMediator
import java.io.Serializable


class DashboardActivity : BaseActivity(), MovieFragment.FirstFragmentToActivity,TvShowsFragment.SecondFragmentToActivity {

    private lateinit var dashboardActivityBinding : ActivityDashboardBinding
    private var totalMovieList:List<Result> = emptyList()
    private var totalTvShowList:List<Result> = emptyList()
    private val tabNameList:List<String> = listOf(DashboardTabList.MOVIES.name, DashboardTabList.TVSHOWS.name)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardActivityBinding = ActivityDashboardBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(dashboardActivityBinding.root)

        setupViewPager(tabNameList)
        setupTabLayout(tabNameList)
        dashboardActivityBinding.ivSearchIcon.setOnClickListener {
            var intent = Intent(this, SearchActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(Constants.KEY_MOVIE_LIST, totalMovieList as Serializable)
            bundle.putSerializable(Constants.KEY_TVSHOW_LIST, totalTvShowList as Serializable)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        dashboardActivityBinding.ivMenuIcon.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    /**
     * this method is overridden to receive movieList from moviesFragment
     */
    override fun onReceivedMoviesData(movieList:List<Result>) {
       totalMovieList = movieList
    }

    /**
     * this method is overridden to receive tvShowList from moviesFragment
     */
    override fun onReceivedTvShowsData(tvShowList: List<Result>) {
        totalTvShowList = tvShowList
    }

    /**
     * This setupTabLayout is responsible for setting up tab layout in main activity.
     * */
    private fun setupTabLayout(tabNameList:List<String>) {
        TabLayoutMediator(
            dashboardActivityBinding.tabLayout, dashboardActivityBinding.viewPager
        ) { tab, position -> tab.text = tabNameList[position]}.attach()
    }

    /**
     * This viewpager is responsible for setting up view pager in main activity.
     * */
    private fun setupViewPager(tabNameList:List<String>) {
        val viewPagerAdapter = ViewPagerAdapter(this, tabNameList.size )
        dashboardActivityBinding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
        }
    }

    /**
     * onBackPressed method is overridden from BaseActivity class, Here we are handling back button click while being on user page.
     * */
    override fun onBackPressed() {
        dashboardActivityBinding.viewPager.let {
            if(it.currentItem != 0){
                it.currentItem = it.currentItem -1
            }
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals(getString(R.string.signup_activity).trim())) {
            checkLoginStatus()
        }
    }
}
