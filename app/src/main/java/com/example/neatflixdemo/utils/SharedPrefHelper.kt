package com.example.neatflixdemo.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Toast
import com.example.neatflixdemo.R
import com.example.neatflixdemo.dataclasses.Dates
import com.example.neatflixdemo.dataclasses.Users
import com.google.gson.Gson
import okhttp3.internal.wait


class SharedPrefHelper {
    companion object {
        private var spNeatflix: SharedPreferences? = null
        private const val KEY_LOGIN_DATA : String= "KEY_LOGIN_DATA"
        private const val SHARED_PREFERENCE_NAME : String= "SP_NEATFLIX"
        private var mContext: Context? = null

        fun verifyLogin(context: Context, userName: String, password: String) {
            mContext = context
            var data:String =
                getSharedPrefObject(context).getString(userName, "").toString()
            if (data.isNotEmpty()) {
                var userData = Gson().fromJson(data, Users::class.java)
                if (userData.password == password) {
                    Utils.showMessage(context, context.getString(R.string.user_authenticated))
                    // Intent dashboard
                } else {
                    Utils.showMessage(context, context.getString(R.string.user_not_found))
                }
            } else {
                Utils.showMessage(context, context.getString(R.string.user_not_found))
            }
        }

        fun getLoginData(context: Context, userName: String, password: String): Users? {
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

        fun setSignUpData(context: Context, spNeatflix: SharedPreferences, users: Users) {
            mContext = mContext
            val data: String = Gson().toJson(users)
            getSharedPrefObject(context).edit().putString(users.email, data)
        }

        fun getSharedPrefObject(context: Context): SharedPreferences {
            if (spNeatflix == null) {
                spNeatflix = context.getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
            }
            return spNeatflix!!
        }
    }
}