package com.example.neatflixdemo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.databinding.ActivitySignUpBinding
import com.example.neatflixdemo.dataclasses.Users
import com.example.neatflixdemo.utils.SharedPrefHelper
import com.example.neatflixdemo.utils.Utils
import java.util.*


class SignUpActivity : BaseActivity() {
    private lateinit var _binding:ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignUpBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(_binding.root)

        _binding.tvRegLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        _binding.btnRegRegister.setOnClickListener{

                if(validCredentials(_binding.etRegUsername.text.toString() ,
                        _binding.etRegPassword.text.toString(),
                        _binding.etRegResetPassword.text.toString(),
                        _binding.etRegAddress.text.toString(),
                        _binding.etRegPhone.text.toString(),_binding.etRegDob.text.toString())) {

                    if(SharedPrefHelper.getSharedPrefObject(this).contains(_binding.etRegUsername.text.toString())){
                        _binding.etRegUsername.error = "user already exists!"
                    }else{
                        val newUser = Users(_binding.etRegUsername.text.toString().toLowerCase() ,
                            "+91 " +_binding.etRegPhone.text.toString(),
                            _binding.etRegDob.text.toString(),
                            _binding.etRegAddress.text.toString(),
                            _binding.etRegPassword.text.toString())
                        SharedPrefHelper.setSignUpData(this , newUser)
                        Utils.showMessage(this, "Signup successful")
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                }else {
                    Utils.showMessage(this, "Enter valid credentials")
                }
        }


    }
    private fun validCredentials(
        username: String,
        password: String,
        resetPassword: String,
        address: String,
        phoneNumber:String,
        dateOfBirth:String
    ): Boolean {
        if (username == "" || password == "" ||
            resetPassword == "" || address == "" ||
            phoneNumber == "" || dateOfBirth == "") {

            return false
        } else if (password != resetPassword) {
            _binding.etRegResetPassword?.error = "password do not match"
            return false
        }else if(!validPhoneNumber(phoneNumber)){
            return false
        }
        return true
    }
    private fun validPhoneNumber(phoneNumber: String):Boolean {
        return Patterns.PHONE.matcher(phoneNumber).matches()
    }

//    fun SignupUser(usernameString: String, passwordString: String?, addressString: String?) {
//        editor?.putString(usernameString + "username", usernameString)
//        editor?.putString(usernameString + "address", addressString)
//        editor?.putString(usernameString + "password", passwordString)
//        editor?.commit()
//    }
}