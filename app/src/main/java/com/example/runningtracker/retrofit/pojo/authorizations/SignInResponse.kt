package com.example.runningtracker.retrofit.pojo.authorizations

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("status")
    val status:String,
    @SerializedName("token")
    val token:String,
    @SerializedName("firstName")
    val firstName:String,
    @SerializedName("lastName")
    val lastName:String,
    @SerializedName("code")
    val error:String
)
