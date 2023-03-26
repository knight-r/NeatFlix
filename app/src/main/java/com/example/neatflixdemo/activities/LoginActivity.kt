package com.example.neatflixdemo.activities

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
    private var count: Int = 0
    private var biometricManager: BiometricManager? = null
    private var isHardwareAvailable:Boolean = true
    private var isFingerprintAvailable:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(loginBinding.root)

        loginBinding.btnLoginSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        loginBinding.btnLoginLogin.setOnClickListener {
            loginUser()
        }
        checkUserLoggedIn()
        loginBinding.ivFingerPrint.setOnClickListener{
            if(SharedPrefHelper.getSharedPrefObject(applicationContext).getBoolean(Constants.KEY_IS_LOGGED_IN,false)) {
                enableBiometricCheck()
            }else {
                Utils.showMessage(this, getString(R.string.please_login_first))
            }
        }

    }

    /**
     * it will check if user is logged in or not
     */
    private fun checkUserLoggedIn() {
        if (SharedPrefHelper.getSharedPrefObject(applicationContext).getBoolean(Constants.KEY_IS_LOGGED_IN,false)) {
            if (!isHardwareAvailable || !isFingerprintAvailable) {
                startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
            } else {
                enableBiometricCheck()
            }
        }
    }

    /**
     * it will enable biometric popup if user has already logged in
     */
    private fun enableBiometricCheck() {
        if (biometricManager == null) {
            biometricManager = BiometricManager.from(this)
        }
        when (biometricManager?.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
               // We don't have to do anything on biometric popup success
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
               isHardwareAvailable = false
                Utils.showMessage(this,getString(R.string.device_doesnot_support_fingerprint))
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                isFingerprintAvailable = false
                Utils.showMessage(this,getString(R.string.biometric_sensor_unavailable))

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                isFingerprintAvailable = false
                Utils.showMessage(this , getString(R.string.enable_fingerprint_in_setting))
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
                    startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))

                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    ++count
                    if (count <= 3) {
                        enableBiometricCheck()
                    } else {
                        biometricPrompt.cancelAuthentication()
                        loginBinding.ivFingerPrint.visibility = View.GONE
                    }
                }
            })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_login_neatflix))
            .setSubtitle(getString(R.string.login_using_biometric))
            .setNegativeButtonText(getString(R.string.login_through_credentials))
            .build()

            biometricPrompt.authenticate(promptInfo)

    }

    /**
     * it will login the user using input username and password
     */
    private fun loginUser(){
        val sharedPreferenceEditor = SharedPrefHelper.getSharedPrefObject(this).edit()
        val userName:String = loginBinding.etLoginUsername.text.toString().toLowerCase()
        val userPassword:String = loginBinding.etLoginPassword.text.toString()
        if(userName == "" || userPassword == "") {
            Utils.showMessage(this, getString(R.string.username_or_password_empty))
        } else if(SharedPrefHelper.verifyLogin(applicationContext, userName, userPassword)){
            sharedPreferenceEditor.putString(Constants.KEY_CURRENT_USER,userName)
            sharedPreferenceEditor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            sharedPreferenceEditor.apply()

            if(count <= 3) {
                enableBiometricCheck()
            } else if( count > 3) {
                startActivity(Intent(this, DashboardActivity::class.java))
            }
        } else {
            Utils.showMessage(this, getString(R.string.user_not_found))
        }
    }

}