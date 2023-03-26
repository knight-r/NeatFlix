package com.example.neatflixdemo.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import com.example.neatflixdemo.R
import com.example.neatflixdemo.constants.Constants
import com.example.neatflixdemo.dataclasses.Dates
import com.example.neatflixdemo.dataclasses.Users
import com.google.gson.Gson
import okhttp3.internal.wait


class SharedPrefHelper {
    companion object {
        private var spNeatflix: SharedPreferences? = null
        private const val SHARED_PREFERENCE_NAME : String= Constants.KEY_SP
        private var mContext: Context? = null

        fun verifyLogin(context: Context, userName: String, password: String): Boolean {
            mContext = context
            var data:String = getSharedPrefObject(context).getString(userName, "").toString()
            return if (data.isNotEmpty()) {
                var userData = Gson().fromJson(data, Users::class.java)
                if (userData.password == password) {
                    Utils.showMessage(context, context.getString(R.string.user_authenticated))
                    true
                } else {
                    Utils.showMessage(context, context.getString(R.string.user_not_found))
                    false
                }
            } else {
                Utils.showMessage(context, context.getString(R.string.user_not_found))
                false
            }
        }

        fun getLoginData(context: Context, userName: String): Users? {
            mContext = context
            var userData: Users? = null
            var spData:String =
                getSharedPrefObject(context).getString(userName, "").toString()
            if (spData!=null && spData.isNotEmpty()) {
                try {
                    userData = Gson().fromJson(spData, Users::class.java)
                } catch (e: Exception) {
                    //ToDO: Nothing to do
                }
            }
            return userData
        }

        fun setSignUpData(context: Context, users: Users) {
            val data: String = Gson().toJson(users)
            val editor = getSharedPrefObject(context).edit()
            editor.putString(users.name, data)
            editor?.commit()
        }
        fun checkCurrentUserStatus(context: Context): Boolean {
            return getSharedPrefObject(context).contains(Constants.KEY_CURRENT_USER) &&
                    getSharedPrefObject(context).getBoolean(Constants.KEY_IS_LOGGED_IN, true)
        }
         fun getSharedPrefObject(context: Context): SharedPreferences {
            if (spNeatflix == null) {
                spNeatflix = context.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
            }
            return context.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        }

    }
}