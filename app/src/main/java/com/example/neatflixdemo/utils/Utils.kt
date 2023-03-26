package com.example.neatflixdemo.utils

import android.content.Context
import android.widget.Toast
class Utils {
    companion object {
        fun showMessage(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun isNetworkAvailable(context: Context) : Boolean {
            return true
        }
    }
}