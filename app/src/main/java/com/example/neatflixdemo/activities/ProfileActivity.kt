package com.example.neatflixdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityProfileBinding
import com.example.neatflixdemo.dataclasses.Users
import com.example.neatflixdemo.utils.SharedPrefHelper

class ProfileActivity : BaseActivity() {
    private lateinit var _binding:ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityProfileBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(_binding.root)

        var sp = SharedPrefHelper.getSharedPrefObject(applicationContext)
        if (!sp.getBoolean(Constants.KEY_IS_LOGGED_IN, false)) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val userName = sp.getString(Constants.KEY_CURRENT_USER,"")
            var userData: Users? =
                userName?.let { SharedPrefHelper.getLoginData(applicationContext, it) }
            userData?.let {
                _binding.tvProfileName.text = it.name
                _binding.tvProfileEmail.text = it.dob
                _binding.tvProfilePhone.text = "  ${it.mobileNumber}"
                _binding.tvProfileAddress.text = "  ${it.address}"
            }
        }
        _binding.backButton.setOnClickListener {
            finish()
        }
        _binding.logoutBtn.setOnClickListener {
            logout(sp)
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (!callingActivity?.className.equals(getString(R.string.signup_activity))) {
            checkLoginStatus()
        }
    }
}