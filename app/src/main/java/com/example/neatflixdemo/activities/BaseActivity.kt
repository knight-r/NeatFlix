package com.example.neatflixdemo.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.utils.SharedPrefHelper

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
    fun checkLoginStatus() {
        val sp = SharedPrefHelper.getSharedPrefObject(applicationContext)
        if(!sp.getBoolean(Constants.KEY_IS_LOGGED_IN, false)){
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
    fun logout(sp: SharedPreferences) {
        val spEdit = sp.edit()
        spEdit.putBoolean(Constants.KEY_IS_LOGGED_IN, false)
        spEdit.putString(Constants.KEY_CURRENT_USER, "")
        spEdit.commit()
    }


}