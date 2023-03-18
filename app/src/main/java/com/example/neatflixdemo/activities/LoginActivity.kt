package com.example.neatflixdemo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.databinding.ActivityLoginBinding
import com.example.neatflixdemo.utils.SharedPrefHelper
import com.example.neatflixdemo.utils.Utils
import java.util.concurrent.Executor


class LoginActivity : BaseActivity() {
    private lateinit var loginBinding :ActivityLoginBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private var fingerVerified = false
    private var count: Int = 0
    private var biometricManager: BiometricManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(loginBinding.root)

        loginBinding.btnLoginSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        loginBinding.btnLoginLogin.setOnClickListener {
//            logout(SharedPrefHelper.getSharedPrefObject(applicationContext))
            loginUser()
        }
        checkUserLoggedIn()
        loginBinding.ivFingerPrint.setOnClickListener{
            if(SharedPrefHelper.getSharedPrefObject(applicationContext).getBoolean(Constants.KEY_IS_LOGGED_IN,false)) {
                enableBiometricCheck()
            }else {
                Utils.showMessage(this, "Please login first")
            }
        }

    }
    private fun checkUserLoggedIn() {
        if(SharedPrefHelper.getSharedPrefObject(applicationContext).getBoolean(Constants.KEY_IS_LOGGED_IN,false)) {
            enableBiometricCheck()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun enableBiometricCheck() {
        if (biometricManager == null) {
            biometricManager = BiometricManager.from(this)
        }
        when (biometricManager?.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Utils.showMessage(this,"Use fingerprint to login")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Utils.showMessage(this,"Device doesn't support fingerprint")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Utils.showMessage(this,"The biometric sensor is unavailable")

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Utils.showMessage(this , "Enable fingerprint in your setting")
            }
        }
        executor = ContextCompat.getMainExecutor(applicationContext)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    ++count
                    if(count<=3) {
                        enableBiometricCheck()
                    } else {
                        biometricPrompt.cancelAuthentication()
                        loginBinding.ivFingerPrint.visibility = View.GONE
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Utils.showMessage(applicationContext, "Authentication succeeded!")
                    startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))

                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    ++count
                    if(count<=3) {
                        enableBiometricCheck()
                    } else {
                        biometricPrompt.cancelAuthentication()
                        loginBinding.ivFingerPrint.visibility = View.GONE
                    }
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Login for Neatflix")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Login Through Credentials")
            .build()

            biometricPrompt.authenticate(promptInfo)

    }

    private fun loginUser(){
        val sharedPreferenceEditor = SharedPrefHelper.getSharedPrefObject(this).edit()
        val userName:String = loginBinding.etLoginUsername.text.toString().toLowerCase()
        val userPassword:String = loginBinding.etLoginPassword.text.toString()
        if(userName == "" || userPassword == "") {
            Utils.showMessage(this, "Username or Password is empty.")
        } else if(SharedPrefHelper.verifyLogin(applicationContext, userName, userPassword)){
            sharedPreferenceEditor.putString(Constants.KEY_CURRENT_USER,userName)
            sharedPreferenceEditor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            sharedPreferenceEditor.commit()

            if(count <= 3) {
                enableBiometricCheck()
            }
            if( count > 3) {
                startActivity(Intent(this, DashboardActivity::class.java))
            }else{
                Utils.showMessage(this, "PLease verify your biometric")
            }
        } else {
            Utils.showMessage(this, "Wrong credentials")
        }
    }
}