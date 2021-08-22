package com.example.runningtracker.database.entity

data class ReminderEntity(
    val id: Int,
    var date: Long,
    var hours: Int,
    var minutes: Int
)