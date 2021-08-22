package com.example.runningtracker.retrofit.pojo.running

import com.google.gson.annotations.SerializedName

data class CompletedTrackResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("id")
    val serverId: Int?,
    @SerializedName("code")
    val error: String?
)