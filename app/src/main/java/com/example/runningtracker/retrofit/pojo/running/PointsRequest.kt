package com.example.runningtracker.retrofit.pojo.running

import com.google.gson.annotations.SerializedName

data class PointsRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("id")
    val id: Int
)
