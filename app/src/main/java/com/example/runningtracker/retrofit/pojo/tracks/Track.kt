package com.example.runningtracker.retrofit.pojo.tracks

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("id")
    var serverId: Int?,
    @SerializedName("beginsAt")
    var beginTime: Long,
    @SerializedName("time")
    var time: Long,
    @SerializedName("distance")
    var distance: Int
)
