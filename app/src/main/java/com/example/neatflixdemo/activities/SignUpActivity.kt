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
    private lateinit var signupBinding:ActivitySignUpBinding
    private val countryCode:String = "+91 "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignUpBinding.inflate(layoutInflater)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background_color_app)
        setContentView(signupBinding.root)

        signupBinding.tvRegLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        chooseDate()
        signupBinding.btnRegRegister.setOnClickListener{

                if(isValidInput(signupBinding.etRegUsername.text.toString() ,
                        signupBinding.etRegPassword.text.toString(),
                        signupBinding.etRegConfirmPassword.text.toString(),
                        signupBinding.etRegAddress.text.toString(),
                        signupBinding.etRegPhone.text.toString(),signupBinding.tvRegDob.text.toString())) {

                    if(SharedPrefHelper.getSharedPrefObject(this).contains(signupBinding.etRegUsername.text.toString())){
                        signupBinding.etRegUsername.error = getString(R.string.user_already_exists)
                    }else{
                        val newUser = Users(signupBinding.etRegUsername.text.toString().toLowerCase() ,
                            countryCode + signupBinding.etRegPhone.text.toString(),
                            signupBinding.tvRegDob.text.toString(),
                            signupBinding.etRegAddress.text.toString(),
                            signupBinding.etRegPassword.text.toString())
                        SharedPrefHelper.setSignUpData(this , newUser)
                        Utils.showMessage(this, getString(R.string.signup_successful))
                        startActivity(Intent(this, LoginActivity::class.java))
                    }

                }else {
                    Utils.showMessage(this, getString(R.string.enter_valid_data))
                }
        }


    }

    /**
     * this method will show the popup calender to choose date of birth
     */
    private fun chooseDate(){
         signupBinding.tvRegDob.setOnClickListener {
             var c = Calendar.getInstance()
             val format = SimpleDateFormat("dd-MM-YYYY")
             val year = c.get(Calendar.YEAR)
             val month = c.get(Calendar.MONTH)
             val day = c.get(Calendar.DAY_OF_MONTH)
             val datePickerDialog = DatePickerDialog(
                 this,
                 { view , year, monthOfYear, dayOfMonth ->
                     c.set(year, monthOfYear, dayOfMonth)
                     signupBinding.tvRegDob.text = format.format(c.time)
                 },
                 year,
                 month,
                 day
             )
             datePickerDialog.datePicker.maxDate = c.timeInMillis
             datePickerDialog.show()
         }
     }

    /**
     * it will check if the data entered in the editTexts are valid are not
     * @return: Boolean
     */
    private fun isValidInput(
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
            signupBinding.etRegConfirmPassword?.error = getString(R.string.password_donot_match)
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