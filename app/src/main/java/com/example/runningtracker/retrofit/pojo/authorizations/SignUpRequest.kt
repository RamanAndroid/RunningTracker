package com.example.runningtracker.retrofit.pojo.authorizations

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email")
    val email:String,
    @SerializedName("firstName")
    val name:String,
    @SerializedName("lastName")
    val secondName:String,
    @SerializedName("password")
    val password:String
)
