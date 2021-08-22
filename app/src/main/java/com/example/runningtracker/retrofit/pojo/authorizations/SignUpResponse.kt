package com.example.runningtracker.retrofit.pojo.authorizations

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("status")
    val status:String,
    @SerializedName("token")
    val token:String,
    @SerializedName("code")
    val error:String
)