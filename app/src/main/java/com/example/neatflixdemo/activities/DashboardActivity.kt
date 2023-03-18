package com.example.neatflixdemo.activities

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.adapter.ViewPagerAdapter
import com.example.neatflixdemo.databinding.ActivityMainBinding
import com.example.neatflixdemo.fragments.FirstFragment
import com.example.neatflixdemo.dataclasses.Result
import com.example.neatflixdemo.fragments.SecondFragment
import com.google.android.material.tabs.TabLayoutMediator
import java.io.Serializable


class DashboardActivity : BaseActivity(), FirstFragment.FirstFragmentToActivity,SecondFragment.SecondFragmentToActivity {

    private lateinit var mainBinding : ActivityMainBinding
    private var totalMovieList:List<Result> = emptyList()
    private var totalTvShowList:List<Result> = emptyList()
    private lateinit var promptInfo :PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(mainBinding.root)

        val tabNameList:List<String> = listOf("Movies","TvShows")

        setupViewPager(tabNameList)
        setupTabLayout(tabNameList)
        mainBinding.ivSearchIcon.setOnClickListener{
            var intent = Intent(this, SearchActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("movie_list", totalMovieList as Serializable)
            bundle.putSerializable("tv_show_list", totalTvShowList as Serializable)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mainBinding.ivMenuIcon.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

    }

    override fun sendData(movieList:List<Result>) {
       totalMovieList = movieList
    }

    override fun sendTvShowData(tvShowList: List<Result>) {
        totalTvShowList = tvShowList
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
        val viewPagerAdapter = ViewPagerAdapter(this, tabNameList.size )
        mainBinding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
        }
    }

    /**
     * onBackPressed method is overridden from Activity class, Here we are handling back button click while being on user page.
     * */
    override fun onBackPressed() {
        mainBinding.viewPager.let {
            if(it.currentItem != 0){
                it.currentItem = it.currentItem -1
            }
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals("SignUpActivity")) {
            checkLoginStatus()
        }
    }
}
