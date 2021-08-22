package com.example.runningtracker.retrofit.pojo.running

import com.example.runningtracker.database.entity.Point
import com.google.gson.annotations.SerializedName

data class CompletedTrackRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("id")
    val serverId:Int?,
    @SerializedName("beginsAt")
    val beginTime: Long,
    @SerializedName("time")
    val time: Long,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("points")
    val points: List<Point>
)
