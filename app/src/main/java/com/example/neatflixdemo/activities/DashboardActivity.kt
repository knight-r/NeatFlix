package com.example.neatflixdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityDashboardBinding
import com.example.neatflixdemo.fragments.MovieFragment
import com.example.neatflixdemo.dataclasses.Result
import com.example.neatflixdemo.enums.DashboardTabList
import com.example.neatflixdemo.fragments.MovieFragmentDirections
import com.example.neatflixdemo.fragments.TvShowsFragment
import com.example.neatflixdemo.fragments.TvShowsFragmentDirections
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
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
        val tabLayout = dashboardActivityBinding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText(tabNameList[0]))
        tabLayout.addTab(tabLayout.newTab().setText(tabNameList[1]))
        dashboardActivityBinding.tabLayout.addOnTabSelectedListener(object :OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if(tab.isSelected && tab.position == 1){
                         val action = MovieFragmentDirections.actionMoviesFragmentToTvshowsFragment()
                         findNavController(R.id.nav_host_fragment).navigate(action)
                    } else {
                        val action = TvShowsFragmentDirections.actionTvshowsFragmentToMoviesFragment()
                        findNavController(R.id.nav_host_fragment).navigate(action)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
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
