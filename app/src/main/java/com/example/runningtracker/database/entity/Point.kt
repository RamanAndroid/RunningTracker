package com.example.runningtracker.database.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Point(
    @SerializedName("lng")
    val lng: Double,
    @SerializedName("lat")
    val lat: Double
): Parcelable
