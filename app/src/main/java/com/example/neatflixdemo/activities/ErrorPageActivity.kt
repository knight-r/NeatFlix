package com.example.neatflixdemo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.neatflixdemo.R

class ErrorPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_page)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

    }
}