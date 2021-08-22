package com.example.runningtracker.retrofit.pojo.running

import com.example.runningtracker.database.entity.Point
import com.google.gson.annotations.SerializedName

data class PointsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("points")
    val pointForData: List<Point>,
    @SerializedName("code")
    val error: String?
)
