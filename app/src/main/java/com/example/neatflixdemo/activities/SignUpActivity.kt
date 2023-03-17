package com.example.neatflixdemo.activities
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R
import com.example.neatflixdemo.databinding.ActivitySignUpBinding
import com.example.neatflixdemo.dataclasses.Users
import com.example.neatflixdemo.utils.SharedPrefHelper
import com.example.neatflixdemo.utils.Utils
import java.text.SimpleDateFormat
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
        chooseDate()
        _binding.btnRegRegister.setOnClickListener{

                if(validCredentials(_binding.etRegUsername.text.toString() ,
                        _binding.etRegPassword.text.toString(),
                        _binding.etRegConfirmPassword.text.toString(),
                        _binding.etRegAddress.text.toString(),
                        _binding.etRegPhone.text.toString(),_binding.tvRegDob.text.toString())) {

                    if(SharedPrefHelper.getSharedPrefObject(this).contains(_binding.etRegUsername.text.toString())){
                        _binding.etRegUsername.error = "user already exists!"
                    }else{
                        val newUser = Users(_binding.etRegUsername.text.toString().toLowerCase() ,
                            "+91 " +_binding.etRegPhone.text.toString(),
                            _binding.tvRegDob.text.toString(),
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
    private fun chooseDate(){
         _binding.tvRegDob.setOnClickListener {
             val c = Calendar.getInstance()
             val format = SimpleDateFormat("dd-MMM-YYYY")

             val year = c.get(Calendar.YEAR)
             val month = c.get(Calendar.MONTH)
             val day = c.get(Calendar.DAY_OF_MONTH)
             val datePickerDialog = DatePickerDialog(
                 this,
                 { view , year, monthOfYear, dayOfMonth ->
                     c.set(year, monthOfYear, dayOfMonth)
                     _binding.tvRegDob.text = format.format(c.time)
                 },
                 year,
                 month,
                 day
             )
             datePickerDialog.show()
         }
     }
    private fun validCredentials(
        username: String,
        password: String,
        resetPassword: String,
        address: String,
        phoneNumber: String,
        dateOfBirth: String,
    ): Boolean {
        if (username == "" || password == "" ||
            resetPassword == "" || address == "" ||
            phoneNumber == "" || dateOfBirth == "") {

            return false
        } else if (password != resetPassword) {
            _binding.etRegConfirmPassword?.error = "password do not match"
            return false
        }else if(!validPhoneNumber(phoneNumber)){
            return false
        }
        return true
    }
    private fun validPhoneNumber(phoneNumber:String):Boolean {
        val phone = phoneNumber.trim { it <= ' ' }
        return Patterns.PHONE.matcher(phone).matches()
    }
    override fun onResume() {
        super.onResume()
    }
}