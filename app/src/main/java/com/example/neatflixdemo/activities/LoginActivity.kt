package com.example.neatflixdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityLoginBinding
import com.example.neatflixdemo.dataclasses.Users
import com.example.neatflixdemo.utils.SharedPrefHelper
import com.example.neatflixdemo.utils.Utils
import com.google.gson.Gson

class LoginActivity : BaseActivity() {
    private lateinit var _binding :ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(_binding.root)
        val sharedPreferenceEditor = SharedPrefHelper.getSharedPrefObject(this).edit()

        _binding.btnLoginSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        _binding.btnLoginLogin.setOnClickListener {
            val userName:String = _binding.etLoginName.text.toString().toLowerCase()
            val userPassword:String = _binding.etLoginPassword.text.toString()
            if(userName == "" || userPassword == "") {
                Utils.showMessage(this, "Username or Password is empty.")
            } else if(SharedPrefHelper.verifyLogin(applicationContext, userName, userPassword)){
                sharedPreferenceEditor.putString(Constants.KEY_CURRENT_USER,userName)
                sharedPreferenceEditor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                sharedPreferenceEditor.commit();
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                Utils.showMessage(this, "Wrong credentials")
            }
        }
    }
}