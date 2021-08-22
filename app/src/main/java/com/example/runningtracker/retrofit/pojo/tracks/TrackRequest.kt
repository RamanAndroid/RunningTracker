package com.example.runningtracker.retrofit.pojo.tracks

import com.google.gson.annotations.SerializedName

data class TrackRequest(
    @SerializedName("token")
    val token: String
)
