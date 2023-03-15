package com.example.neatflixdemo.dataclasses

data class Users(
    var name: String,
    val mobileNumber: String,
    val dob: String,
    val address: String,
    val password: String
)