package com.example.runningtracker.database.entity

data class TrackEntity(
    var id: Int,
    var serverId: Int?,
    var beginTime: Long,
    var time: Long,
    var distance: Int
)