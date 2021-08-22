package com.example.runningtracker.retrofit.pojo.tracks

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("tracks")
    val trackForData: List<Track>,
    @SerializedName("code")
    val error: String?
)
